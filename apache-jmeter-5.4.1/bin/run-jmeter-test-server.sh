./jmeter-server -DTEST_AUTO=true -Djava.rmi.server.hostname=${MY_LOCAL_IP} > jmeter-server.out &
pkill -9 -ef com.auto.test.jmeter.plugin.common.server.ShellServer
java -cp .:../lib/ext/test-automation-jmeter-plugin.jar com.auto.test.jmeter.plugin.common.server.ShellServer &
