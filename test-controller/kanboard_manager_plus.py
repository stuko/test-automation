from kanboard_manager import KanboardManager

class KanboardManagerPlus(KanboardManager):
    def create_project(self, project_name):
        project_id = self.kb.create_project(name=project_name)
    def get_available_actions(self):
        return self.kb.get_available_actions()
    def get_available_action_events(self):
        return self.kb.get_available_action_events()
    def get_compatible_action_events(self,action_name):
        action_name = f'\\Kanboard\\Action\\{action_name}'
        # params = [f'\\Kanboard\\Action\\{action_name}']
        return self.kb.get_compatible_action_events(action_name)
    def get_actions(self,id):
        return self.kb.get_actions(id)
    
if __name__ == '__main__':
    kmp = KanboardManagerPlus('192.168.57.224','8080','6924abf4f501bc242e466218edf8a1af67c5f6b68efc034df6e4ff8e8777','kanboard','kanboard-secret','kanboard')
    # kmp.create_project('TEST_PROJECT')
    # print(kmp.get_available_actions())
    # print(kmp.get_available_action_events())
    print(kmp.get_compatible_action_events('TaskDuplicateAnotherProject'))
    # print(kmp.get_actions(1))
    
    
    