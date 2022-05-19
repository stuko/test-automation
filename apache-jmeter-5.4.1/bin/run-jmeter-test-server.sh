export MY_LOCAL_IP=`hostname -I | cut -d' ' -f1`
sudo pkill -9 -ef jmeter-server
sleep 1
sudo chmod a+x *.sh jmeter*
sudo nohup ./jmeter-server -DTEST_AUTO=true -Djava.rmi.server.hostname=${MY_LOCAL_IP} > jmeter-server.out &
sudo pkill -9 -ef com.auto.test.jmeter.plugin.common.server.ShellServer
sleep 1
sudo nohup java -cp .:../lib/ext/test-automation-jmeter-plugin.jar com.auto.test.jmeter.plugin.common.server.ShellServer &
