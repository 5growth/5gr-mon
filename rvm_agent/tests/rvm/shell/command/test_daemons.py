import logging

import pytest
from click.testing import CliRunner

import rvm_agent.shell.main as rvm_agent

logging.basicConfig(level='INFO')


#@pytest.mark.skip
def test_daemon_create():
    runner = CliRunner(env={
                            "BOOTSTRAP_SERVERS": "localhost",
                            "FORWARD_TOPIC": "vm_agent_1_forward",
                            "BACKWARD_TOPIC": "vm_agent_1_backward",
                            "LOGFILE": "vm_agent.log",
                            "RVM_DAEMON_NAME": "vyos_vm_7byyfa",
                            "RVM_DAEMON_USER": "ktomakh",
                            "RVM_DAEMON_WORKDIR": "ktomakh"
                            })
    result = runner.invoke(rvm_agent.main, ['daemon', 'configure'],  catch_exceptions=False)
    # assert result.exit_code == 0
    # assert result.output == ''

@pytest.mark.skip
def test_daemon_configure():
    runner = CliRunner(env={
                            "RVM_DAEMON_NAME": "vyos_vm_7byyfa",
                            "RVM_DAEMON_USER": "ktomakh"
                            })
    result = runner.invoke(rvm_agent.main, ['daemon', 'configure'],  catch_exceptions=False)
    print("test_daemon_configure")
    # assert result.exit_code == 0

#@pytest.mark.skip
def test_daemon_start():
    runner = CliRunner(env={
                            "RVM_DAEMON_NAME": "vyos_vm_7byyfa",
                            })
    result = runner.invoke(rvm_agent.main, ['daemon', 'start'],  catch_exceptions=False)


@pytest.mark.skip
def test_daemon_stop():
    runner = CliRunner(env={
                            "RVM_DAEMON_NAME": "vyos_vm_7byyfa",
                            })
    result = runner.invoke(rvm_agent.main, ['daemon', 'stop'],  catch_exceptions=False)


@pytest.mark.skip
def test_daemon_delete():
    runner = CliRunner(env={
                            "RVM_DAEMON_NAME": "vyos_vm_7byyfa",
                            })
    result = runner.invoke(rvm_agent.main, ['daemon', 'delete'],  catch_exceptions=False)

