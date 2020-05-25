import os
import shlex
import subprocess
import sys
import tempfile
import uuid
import logging

import pkg_resources
from jinja2 import Template

RVM_PREFIX = 'rvm-agent'


class SystemdScript:
    def __init__(self):
        self.value = ""
        self.lgr = logging.getLogger()
        self.VIRTUALENV = os.path.dirname(os.path.dirname(sys.executable))
        self.user = ''
        self.name = ''
        self.extra_env_path = None
        self.broker_ip = []
        self.workdir = ""

    @property
    def config_path(self):
        """
            Returns daemon config path for RVM agent
        :return: String
        """
        return '/etc/sysconfig/rvm-agent-' + self.name

    @property
    def script_path(self):
        """
            Returns daemon script path for RVM agent
        :return: String
        """
        return '/usr/lib/systemd/system/rvm-agent-' + self.name + '.service'

    @property
    def agent_config(self):
        """
            Returns config path for RVM agent
        :return: String
        """
        return "/home/" + self.user + "/" + self.name + "/" + self.name + ".json"

    @property
    def service_name(self):
        """
            Returns Linux daemon name for RVM agent
        :return: String
        """
        return "rvm-agent-" + self.name

    def load_data_from_dict(self, params):
        """
            Copies data for generating configuration Linux daemon for RVM agent
        """
        self.user = params.get('user')
        self.name = params.get('name')
        self.workdir = params.get('workdir')

    def get_resource(self, resource_path):
        """
            Returns file's content from directory "resources"
        """
        return pkg_resources.resource_string(
            __name__,
            os.path.join(resource_path)
        ).decode("utf-8")

    def content_to_file(self, content):

        """
            Saves content to a temporary file and returns the path for this file
        """

        tempdir = tempfile.gettempdir()
        file_path = tempfile.NamedTemporaryFile(
            mode='w',
            delete=False,
            dir=tempdir).name
        with open(file_path, 'w') as f:
            f.write(content)
            f.write(os.linesep)
        return file_path

    def render_template_to_file(self, template_path, **values):
        """
         Render it with  variables "values"
         :type values: dictionary of values for template
         :type template_path: String
        """
        # gets Template body
        template = self.get_resource(template_path)
        rendered = Template(template).render(**values)
        # saves the result to a temporary file and return path to this file
        return self.content_to_file(rendered)

    def _systemctl_command(self, command):
        """
            Generates command systemd for RVM agent
        """
        return 'sudo systemctl {command} {service}'.format(
            command=command,
            service=self.service_name,
        )

    def _command_split(self, command):
        """
            Splits command string into parts and saves them in the list.
        """
        lex = shlex.shlex(command, posix=True)
        lex.whitespace_split = True
        lex.escape = ''
        return list(lex)

    def generate_agent_name(self):
        """Generates a unique name with a pre-defined prefix
        """
        return '{0}-{1}'.format(RVM_PREFIX, uuid.uuid4())

    def _get_rendered_config(self):
        """
        Generates configuration for linux daemon
        :return: The path for generated daemon config
        """
        return self.render_template_to_file(
            template_path='resources/systemd/systemd.conf.template',
            agent_config=self.agent_config,
            workdir=self.workdir,
            name=self.name
        )

    def _get_rendered_script(self):
        """
        Generates configuration for linux daemon and copy to system directory
        """
        self.lgr.debug('Rendering SystemD script from template')
        return self.render_template_to_file(
            template_path='resources/systemd/systemd.template',
            virtualenv_path=self.VIRTUALENV,
            user=self.user,
            config_path=self.config_path,
            name=self.name,
            extra_env_path=self.extra_env_path,

        )

    def create_script(self):
        """
            Generates script for Linux daemon and copy to system directory
        """
        rendered = self._get_rendered_script()
        # creates directoty for linux daemon script
        self.run('sudo mkdir -p {0}'.format(
            os.path.dirname(self.script_path)))
        self.run(
            'sudo cp {0} {1}'.format(rendered, self.script_path))
        self.run('sudo rm {0}'.format(rendered))

    def create_config(self):
        """
            Generates config for Linux daemon and copies to the system directory
        """
        rendered = self._get_rendered_config()
        # creates directoty for linux daemon config
        self.run('sudo mkdir -p {0}'.format(
            os.path.dirname(self.config_path)))
        self.run('sudo cp {0} {1}'.format(rendered, self.config_path))
        self.run('sudo rm {0}'.format(rendered))

    def run(self, command,
            exit_on_failure=False,
            cwd=None,
            execution_env=None):

        """
            Executes commands
        """
        if isinstance(command, list):
            popen_args = command
        else:
            popen_args = self._command_split(command)
        stdout = subprocess.PIPE
        stderr = subprocess.PIPE
        command_env = os.environ.copy()
        command_env.update(execution_env or {})
        p = subprocess.Popen(args=popen_args, stdout=stdout,
                             stderr=stderr, cwd=cwd, env=command_env)
        out, err = p.communicate()
        if out:
            out = out.rstrip()
        if err:
            err = err.rstrip()

        if p.returncode != 0:
            error = {
                "command": command,
                "error": err,
                "output": out,
                "code": p.returncode}
            if exit_on_failure:
                raise error
            else:
                self.lgr.error(error)

        return {
            "command": command,
            "std_out": out,
            "std_err": err,
            "return_code": p.returncode}

    def start_command(self):
        """
        Returns command to start RVM Agent daemon
        :rtype: String returns command to start RVM Agent daemon
        """
        if not os.path.isfile(self.script_path):
            self.lgr.error("Daemon is not configurated")
        return self._systemctl_command('start')

    def create_daemon(self):
        """
         Creates configuration and script for RVM Agent Linux daemon.
        """
        self.create_script()
        self.create_config()

    def start_daemon(self):
        """
         Strats RVM Agent Linux daemon.
        """
        self.run(self._systemctl_command('enable'))
        self.run('sudo systemctl daemon-reload')
        self.run(self._systemctl_command('start'))

    def stop_daemon(self):
        """
         Stops RVM Agent Linux daemon.
        """
        self.run(self._systemctl_command('stop'))

    def delete_daemon(self):
        """
         Deletes RVM Agent Linux daemon.
        """
        self.run(self._systemctl_command('disable'))
        if os.path.exists(self.script_path):
            self.run('sudo rm {0}'.format(self.script_path))
        if os.path.exists(self.config_path):
            self.run('sudo rm {0}'.format(self.config_path))
