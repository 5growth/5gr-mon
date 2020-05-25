import click
from rvm_agent.shell.command import daemon, configure


@click.group()
def main():
    pass


@click.group('daemon')
def daemon_sub_command():
    pass


main.add_command(configure.configure)
daemon_sub_command.add_command(daemon.create)
daemon_sub_command.add_command(daemon.configure)
daemon_sub_command.add_command(daemon.start)
daemon_sub_command.add_command(daemon.stop)
daemon_sub_command.add_command(daemon.delete)

main.add_command(daemon_sub_command)
