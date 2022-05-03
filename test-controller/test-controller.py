from flask import Flask, request, jsonify
from mattermost_manager import MatterMostManager
from jmeter_manager import JmeterManager
from kanboard_manager import KanboardManager
from jenkins_manager import JenkinsManager
from os.path import exists
import time
import json
import datetime
import os, sys, socket

from pymongo import MongoClient
from pymongo.cursor import CursorType

app = Flask(__name__) 
config_file = "./volume/config/config.json"
upload_folder = "./volume/upload/"

@app.route('/controller', methods=['POST']) 
def controller():
    print('------ start parameter !!! ---------')
    print(request.data) 
    print('------ end parameter !!! -----')
    return 'controller!' 

@app.route('/backlog') 
def backlog(): 
    return 'backlog!' 

@app.route('/sprintlog') 
def sprintlog(): 
    return 'spring log!!!!!' 

@app.route('/develop') 
def develop(): 
    return 'develop!!!!' 

@app.route('/testing') 
def testing(): 
    return 'testing..!' 

@app.route('/review') 
def review(): 
    params = request.get_json()
    return km.move_to_forward(params)

@app.route('/release') 
def release(): 
    return 'release.....!' 

@app.route('/get_project_list', methods=['POST','GET']) 
def get_project_list(): 
    return km.get_project_lists()

@app.route('/get_project_detail', methods=['POST','GET']) 
def get_project_detail():
    params = request.get_json() 
    return jm.get_project_detail(params['project_id'])

@app.route('/get_project_detail_by_jmx', methods=['POST','GET']) 
def get_project_detail_by_jmx():
    params = request.get_json() 
    result = json.dumps(jm.get_project_detail_by_jmx(params['jmx_file_name']))
    return result


@app.route('/save_project_info', methods=['POST','GET']) 
def save_project_info(): 
    params = request.get_json()
    return jm.save_project_info(params)


@app.route('/save_factors', methods=['POST','GET']) 
def save_factors(): 
    params = request.get_json()
    return jm.save_factors(params)

@app.route('/save_run_config', methods=['POST','GET']) 
def save_run_config(): 
    params = request.get_json()
    return jm.save_run_config(params)

    
@app.route("/upload", methods=["POST"])
def upload():
    file = request.files['file']
    os.makedirs(upload_folder, exist_ok=True)
    file.save(os.path.join(upload_folder, file.filename))
    return "upload"

    
if __name__ == '__main__': 
    
    mongo_ip = socket.gethostbyname(socket.gethostname())
    mongo_port = 27017
    
    file_exists = exists(config_file)
    
    if file_exists : 
        
        f = open(config_file,'r')
        config_json = json.load(f)
        
        if config_json['mongo_ip'] != None and config_json['mongo_port'] != None :
            # 몽고 DB에 접속한다.
            # 입력받은 IP와 PORT로 접속한다.
            # Config 정보를 읽어 온다.
            # 없으면, Default를 생성한다.
            '''
            config :
                flask { port : },
                kanboard { ip :  , port : , token : , id : , pw : , db : },
                mattermost { url :  },
                jmeter { path : }
            '''
            # X : Flask Port
            # Mongodb IP, Port 
            # Upload Path
            # kanboard (ip, port, token , id, pw, db)
            # mattermost (url)
            # JMeter (Mongo ip, Mongo port, jmeter_path)
            mongo_ip = config_json['mongo_ip']
            mongo_port = config_json['mongo_port']
        
    print(f'Mongo ip is {mongo_ip} , port is {str(mongo_port)}')
        
    mongo = MongoClient(mongo_ip, mongo_port)
    db = mongo['auto']
    config = db['config']
    collection = config.find()[0]
    print(collection)
    
    if collection != None :
        
        flask_port = collection['flask']['port']
        kanboard_ip = collection['kanboard']['ip']
        kanboard_port = collection['kanboard']['port']
        kanboard_token = collection['kanboard']['token']
        kanboard_id = collection['kanboard']['id']
        kanboard_pw = collection['kanboard']['pw']
        kanboard_db = collection['kanboard']['db']
        mattermost_url = collection['mattermost']['url']
        jmeter_path = collection['jmeter']['path']
        
        global km , mm, jm, jkm
        km = KanboardManager(kanboard_ip,str(kanboard_port),kanboard_token,kanboard_id,kanboard_pw,kanboard_db)
        jm = JmeterManager(mongo_ip,mongo_port,jmeter_path)
        mm = MatterMostManager(mattermost_url, jm)
        jkm = JenkinsManager(jm)
    
        app.run(host="0.0.0.0",debug=True, port=flask_port)
    else :
        print("Please input config info to MongoDB")
