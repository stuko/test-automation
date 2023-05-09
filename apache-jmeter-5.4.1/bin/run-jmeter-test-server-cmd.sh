export MY_LOCAL_IP=`hostname -I | cut -d' ' -f1`
pkill -9 -ef jmeter-server
sleep 1
chmod a+x *.sh jmeter*
nohup ./jmeter-server -DTEST_AUTO=true -Djava.rmi.server.hostname=${MY_LOCAL_IP} > jmeter-server.out &
