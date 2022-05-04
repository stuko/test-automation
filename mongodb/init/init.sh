sudo docker exec -it mongo-test-automation /bin/bash
mongo
use auto
db.createCollection('config')
db.config.remove({})
db.config.insert(
    {
        flask : { port : NumberInt(5000)},
        kanboard : { ip : '192.168.57.224' , port : NumberInt(8080), token : '6924abf4f501bc242e466218edf8a1af67c5f6b68efc034df6e4ff8e8777' , id : 'kanboard', pw : 'kanboard-secret', db : 'kanboard'},
        mattermost : { url : 'http://192.168.57.237:8065/hooks/' },
        jmeter : { path : '../apache-jmeter-5.4.1/bin/'}
    }
)
exit
exit
