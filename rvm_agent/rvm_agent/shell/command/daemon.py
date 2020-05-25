import logging
import os

import click
import json

from rvm_agent.daemon.systemd_script import SystemdScript
from rvm_agent.shell import env

logging.basicConfig(level='INFO')


@click.option('bootstrap_servers', '--bootstrap_servers',
              help='The IP or host name of kafka servers'
              .format(env.BOOTSTRAP_SERVERS),
              required=True,
              envvar=env.BOOTSTRAP_SERVERS)
@click.option('forward_topic', '--forward_topic',
              help='The topic from kafka server to agent'
              .format(env.FORWARD_TOPIC),
              required=True,
              envvar=env.FORWARD_TOPIC)
@click.option('backward_topic', '--backward_topic',
              help='The topic from agent to kafka server'
              .format(env.BACKWARD_TOPIC),
              required=True,
              envvar=env.BACKWARD_TOPIC)
@click.option('logfile', '--logfile',
              help='The name of logfile in remote VM agent'
              .format(env.LOGFILE),
              required=True,
              envvar=env.LOGFILE)
@click.option('user', '--user',
              help='The user to create this daemon under. [env {0}]'
              .format(env.RVM_DAEMON_USER),
              envvar=env.RVM_DAEMON_USER)
@click.option('workdir', '--workdir',
              help='Working directory for runtime files (pid, log). '
                   'Defaults to current working directory. [env {0}]'
              .format(env.RVM_DAEMON_WORKDIR),
              envvar=env.RVM_DAEMON_WORKDIR)
@click.option('--name',
              help='The name of the daemon. [env {0}]'
              .format(env.RVM_DAEMON_NAME),
              envvar=env.RVM_DAEMON_NAME,
              required=True)
@click.command()
def create(**params):
    """
        Create daemon.
        Uses value --name and --user
        Saves values to configuration file /home/user/rvm_name/rvm_name.json
        Creates directory /home/user_name/rvm_name/work
    """
    rvm_name = params.get('name')
    rvm_user = params.get('user')
    rvm_agent_home_dir = "/home/" + rvm_user + "/" + rvm_name
    rvm_agent_work_dir = rvm_agent_home_dir + "/work"
    # Checks and creates home directory for RVM Agent
    if not os.path.exists(rvm_agent_home_dir):
        os.makedirs(rvm_agent_home_dir)
    # Checks and creates home directory for RVM Agent
    if not os.path.exists(rvm_agent_work_dir):
        os.makedirs(rvm_agent_work_dir)
    daemon_path = os.path.join(
        rvm_agent_home_dir, '{0}.json'.format(
            rvm_name)
    )
    # Saves configuration file
    with open(daemon_path, 'w') as f:
        json.dump(params, f, indent=2)
        f.write(os.linesep)


@click.option('--name',
              help='The name of the daemon. [env {0}]'
              .format(env.RVM_DAEMON_NAME),
              required=True,
              envvar=env.RVM_DAEMON_NAME)
@click.option('--user',
              help='The user to load the configuration from. Defaults to '
                   'current user. [env {0}]'
              .format(env.RVM_DAEMON_USER),
              envvar=env.RVM_DAEMON_USER)
@click.command()
def configure(name, user=None):
    """
        Creates configuration files for linux daemon
        Uses value --name and --user
    """
    rvm_name = name
    rvm_user = user
    rvm_agent_home_dir = "/home/" + rvm_user + "/" + rvm_name
    json_config_file = rvm_agent_home_dir + "/" + rvm_name + ".json"
    if os.path.exists(json_config_file):
        with open(json_config_file) as f:
            params = json.load(f)
    systemd_script = SystemdScript()
    # Change user owner to rvm_user for whole directory RVM agent
    systemd_script.run('sudo chown -R  {0}:{0} {1}'.format(rvm_user, rvm_agent_home_dir))
    systemd_script.load_data_from_dict(params)
    systemd_script.create_daemon()


@click.option('--name',
              help='The name of the daemon. [env {0}]'
              .format(env.RVM_DAEMON_NAME),
              required=True,
              envvar=env.RVM_DAEMON_NAME)
@click.command()
def start(name):
    """
        Starts Linux daemon
    """
    rvm_name = name
    systemd_script = SystemdScript()
    systemd_script.name = rvm_name
    systemd_script.start_daemon()


@click.option('--name',
              help='The name of the daemon. [env {0}]'
              .format(env.RVM_DAEMON_NAME),
              required=True,
              envvar=env.RVM_DAEMON_NAME)
@click.command()
def stop(name):
    """
        Stops Linux daemon
    """
    rvm_name = name
    systemd_script = SystemdScript()
    systemd_script.name = rvm_name
    systemd_script.stop_daemon()


@click.option('--name',
              help='The name of the daemon. [env {0}]'
              .format(env.RVM_DAEMON_NAME),
              required=True,
              envvar=env.RVM_DAEMON_NAME)
@click.command()
def delete(name):
    """
        Deletes configuration files for linux daemon
    """
    rvm_name = name
    systemd_script = SystemdScript()
    systemd_script.name = rvm_name
    systemd_script.delete_daemon()
