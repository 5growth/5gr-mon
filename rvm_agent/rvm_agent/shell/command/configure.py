import logging
import os
import sys
import click

from virtualenv import (OK_ABS_SCRIPTS, path_locations,
                        fixup_pth_and_egg_link, relative_script)
ENV_EXEC_TEMPDIR = 'EXEC_TEMP'

from rvm_agent import VIRTUALENV

@click.command()
@click.option('--relocated-env',
              help='Change python path env',
              is_flag=True)
def configure(relocated_env):
    if relocated_env:
        click.echo('Auto-correcting virtualenv {0}'.format(VIRTUALENV))
        _fix_virtualenv()
        click.echo('Successfully corrected rvm-agent`s virtualenv')

def _fix_virtualenv():
    _make_environment_relocatable(VIRTUALENV)


def _make_environment_relocatable(home_dir):
    home_dir, lib_dir, inc_dir, bin_dir = path_locations(home_dir)

    _fixup_scripts(bin_dir)
    fixup_pth_and_egg_link(home_dir)


def _fixup_scripts(bin_dir):

    logger = logging.getLogger()

    new_path_system_env = _get_path_system_env()
    for filename in _get_files_to_fix(bin_dir):
        logger.debug('Making script {0} relative'.format(filename))
        _rewrite_env_path(filename, new_path_system_env)


def _get_files_to_fix(bin_dir):

    for filename in os.listdir(bin_dir):

        if filename in OK_ABS_SCRIPTS:
            continue

        filename = os.path.join(bin_dir, filename)
        if not os.path.isfile(filename):
            continue

        with open(filename, 'rb') as f:
            try:
                lines = f.read().decode('utf-8').splitlines()
            except UnicodeDecodeError:
                continue

        if not lines:
            continue

        first_line = lines[0]
        if not (first_line.startswith('#!') and 'bin/python' in first_line):
            continue

        yield filename


def _rewrite_env_path(filename, new_env_path):
    with open(filename, 'rb') as f:
        lines = f.read().decode('utf-8').splitlines()

    script = relative_script([new_env_path] + lines[1:])

    with open(filename, 'wb') as f:
        f.write('\n'.join(script).encode('utf-8'))


def _get_path_system_env():
    envpath = '/usr/bin/env'
    ver = sys.version[:3]
    extension = ''

    return '#!{0} python{1}{2}'.format(envpath, ver, extension)
