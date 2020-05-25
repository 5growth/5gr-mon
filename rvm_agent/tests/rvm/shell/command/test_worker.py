import logging

import pytest
from click.testing import CliRunner
from rvm_agent import worker

logging.basicConfig(level='INFO')


#@pytest.mark.skip
def test_worker_run():
    runner = CliRunner(env={
                            "AGENT_CONFIG": "/home/ktomakh/vyos_vm_7byyfa/vyos_vm_7byyfa.json"
                            })
    result = runner.invoke(worker.main, catch_exceptions=False)
    print(result)
