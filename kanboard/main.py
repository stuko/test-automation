import subprocess
import time
import os
import runpy
from multiprocessing import Pool, Process, freeze_support

def execute(process):                                                             
    os.system(f'python {process}')    

if __name__=='__main__':
    process_pool = Pool(processes = 4)   
    try:
        freeze_support() #
        
        # subprocess.call("sub1.py", shell=True)
        # subprocess.call("sub2.py", shell=True)
        # os.system("python sub1.py")
        # os.system("python sub2.py")
        # runpy.run_path(path_name='sub1.py')
        # runpy.run_path(path_name='sub2.py')
        all_processes = ('sub1.py', 'sub2.py')
        # process_pool.map(execute, all_processes)
        process_pool.map_async(execute, all_processes)

        # while True:
        #   print("----------------") 
        #   time.sleep(1)
         
        process_pool.close()
        process_pool.join()
    except KeyboardInterrupt:
        # process_pool.close()
        print ("Process stopped")
    finally:
        print ("Ok will be exit")