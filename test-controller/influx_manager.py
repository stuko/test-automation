from influxdb import InfluxDBClient

#######################################
# Not Need : Grafana can support this function
#######################################
class InfluxManager:
    
    def __init__(self, host='localhost',port=8086,user='root', password='root', dbname='jmeter'):
        print("init")
        client = InfluxDBClient(host, port, user, password, dbname)
        if client.describeDatabases().contains(dbname) == False:
            client.create_database(dbname)    
                        
        
if __name__ == '__main__' :
    influx = InfluxManager()    
    
    