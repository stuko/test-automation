import base64
import kanboard
import json
import mysql.connector
import asyncio

# 프러덕트 백로그 -> 스프린트 백로그 -> 개발 -> 테스트 자동화 -> 테스트 리뷰 -> 테스트 완료 -> 릴리즈

class KanboardManager:
    
    def __init__(self , ip, port, token , id, pw, db):
        self.kb = kanboard.Client(f'http://{ip}:{port}/jsonrpc.php', 'jsonrpc', token)        
        self.ip = ip
        self.user = id
        self.pwd = pw
        self.db = db
        self.charset = 'utf8'
        self.column_titles = ['프러덕트 백로그','스프린트 백로그','개발','테스트 자동화','테스트 리뷰','테스트 완료','릴리즈']

    def check_project_is_scrum(self, project_id):
        columns = self.kb.get_columns(project_id=project_id)
        
        if len(columns) > len(self.column_titles):
            for i in range(len(columns),len(self.column_titles)-1,-1):
                self.kb.remove_column(project_id=project_id,column_id=i)

        if len(columns) < len(self.column_titles):
            print(f"{len(columns)} vs {len(self.column_titles)}")
            for i in range(len(columns),len(self.column_titles)):
                print(f'add column {self.column_titles[i]}')    
                self.kb.add_column(project_id=project_id,title=self.column_titles[i])
        
        if (columns[0]["title"] != self.column_titles[0] or columns[1]["title"] != self.column_titles[1] or columns[2]["title"] != self.column_titles[2] or columns[3]["title"] != self.column_titles[3] or columns[4]["title"] != self.column_titles[4] or columns[5]["title"] != self.column_titles[5] or columns[6]["title"] != self.column_titles[6]) :
            print(f'colums length of project is {len(self.column_titles)}')
            for i in range(0,len(self.column_titles)):
                print(f'project_id is {project_id}, column_id is {columns[i]["id"]}')
                self.kb.update_column(project_id=project_id,column_id=columns[i]["id"],title=self.column_titles[i])

    def get_connection(self, ip, user, pwd, db, charset):
        return mysql.connector.connect(host=ip,user=user,password=pwd,db=db,charset=charset)

    def get_project_lists(self):
        project_list = self.kb.get_all_projects()
        return project_list  

    def get_column(self, id):
        return self.column_titles[id]

    def get_next_position(self, project_id, task_id , step):
        con = self.get_connection(self.ip,self.user,self.pwd,self.db,self.charset)
        cur = con.cursor(dictionary=True)
        sql = """
        select id
        from   columns
        where  project_id = %s
        and    position  in 
        (select position + %s
        from   columns
        where  id in (select column_id 
                            from tasks
                            where id = %s) 
        )
        """
        input = (project_id, step, task_id,)
        cur.execute(sql, input)
        data = cur.fetchall()
        con.close()
        return data[0]['id']
    
    def move_to_forward(self, project_id, task_id, swimlane_id, column_id, position):
        project = self.kb.get_project_by_id(project_id=project_id)
        target_task = self.kb.get_task(task_id = task_id)
        id = self.get_next_position(project_id, task_id , 1)
        self.kb.move_task_position(project_id=project_id,swimlane_id=swimlane_id,task_id=task_id,column_id=id,position=position)
        return 'review'   
    
    def move_to_backward(self, project_id, task_id, swimlane_id, column_id, position):
        project = self.kb.get_project_by_id(project_id=project_id)
        target_task = self.kb.get_task(task_id = task_id)
        id = self.get_next_position(project_id, task_id , -1)
        self.kb.move_task_position(project_id=project_id,swimlane_id=swimlane_id,task_id=task_id,column_id=id,position=position)
        return 'review'   
    
    def move(self, project_id, task_id, swimlane_id, column_id , position):
        project = self.kb.get_project_by_id(project_id=project_id)
        target_task = self.kb.get_task(task_id = task_id)
        self.kb.move_task_position(project_id=project_id,swimlane_id=swimlane_id,task_id=task_id,column_id=column_id, position=position)
        return 'move'   
    

