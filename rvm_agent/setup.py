from distutils.core import setup

setup(
    name='rvm_agent',
    version='0.01',
    packages=[
        'rvm_agent',
        'rvm_agent.daemon',
        'rvm_agent.shell',
        'rvm_agent.shell.command'
        ],
package_data={
        'rvm_agent': [
            'VERSION',
            'daemon/resources/disable-requiretty.sh',
            'daemon/resources/crontab/disable.sh.template',
            'daemon/resources/crontab/enable.sh.template',
            'daemon/resources/respawn.sh.template',
            'daemon/resources/systemd/systemd.conf.template',
            'daemon/resources/systemd/systemd.template',
            'daemon/resources/script/linux.sh.template',
            'daemon/resources/script/windows.ps1.template',
            'daemon/resources/script/linux-download.sh.template',
            'daemon/resources/script/windows-download.ps1.template',
            'daemon/resources/script/stop-agent.py.template'
        ]
    },
    url='',
    license='',
    author='ktomakh',
    author_email='',
    description='',
    entry_points={
            'console_scripts': [
                'rvm-agent = rvm_agent.shell.main:main',
                'worker = rvm_agent.worker:main'
            ]
        }


)
