pipeline {
	agent {label "test-automation"}
	tools {
		gradle "Gradle 4.4"
	}
	stages {
        stage('git-pull'){
            parallel { 
                stage('git-pull-1') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: "master"]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'kai-package']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://admin@192.168.57.237:19000/KAI-MS/deployment']]
                                                        ])}}
                stage('git-pull-2') {steps {checkout([$class: 'SubversionSCM', 
                                                        additionalCredentials: [], 
                                                        excludedCommitMessages: '', 
                                                        excludedRegions: '', 
                                                        excludedRevprop: '', 
                                                        excludedUsers: '', 
                                                        filterChangelog: false, 
                                                        ignoreDirPropChanges: false, 
                                                        includedRegions: '', 
                                                        locations: [[cancelProcessOnExternalsFail: true, 
                                                                        credentialsId: 'admin', 
                                                                        depthOption: 'infinity', 
                                                                        ignoreExternalsOption: true, 
                                                                        local: './pylon-resource', 
                                                                        remote: 'http://192.168.57.237:19000/svn/COMMON/pylon-framework/branches/v2.0.0/pylon-framework/pylon-framework-manager-webapp/src/main/resources']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}
                stage('git-pull-3') {steps {checkout([$class: 'SubversionSCM', 
                                                        additionalCredentials: [], 
                                                        excludedCommitMessages: '', 
                                                        excludedRegions: '', 
                                                        excludedRevprop: '', 
                                                        excludedUsers: '', 
                                                        filterChangelog: false, 
                                                        ignoreDirPropChanges: false, 
                                                        includedRegions: '', 
                                                        locations: [[cancelProcessOnExternalsFail: true, 
                                                                    credentialsId: 'admin', 
                                                                    depthOption: 'infinity', 
                                                                    ignoreExternalsOption: true, 
                                                                    local: './access-logger', 
                                                                    remote: 'http://192.168.57.237:19000/svn/COMMON/pylon-framework/pylon-action-logger/koreacb-logger/target']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}
                stage('git-pull-4') {steps {checkout([$class: 'SubversionSCM', 
                                                        additionalCredentials: [], 
                                                        excludedCommitMessages: '', 
                                                        excludedRegions: '', 
                                                        excludedRevprop: '', 
                                                        excludedUsers: '', 
                                                        filterChangelog: false, 
                                                        ignoreDirPropChanges: false, 
                                                        includedRegions: '', 
                                                        locations: [[cancelProcessOnExternalsFail: true, 
                                                                        credentialsId: 'admin', 
                                                                        depthOption: 'infinity', 
                                                                        ignoreExternalsOption: true, 
                                                                        local: './cc-admin-resource', 
                                                                        remote: 'http://192.168.57.237:19000/svn/COMMON/commandCenter/cc-admins/cc-admin-webapp/src/main/resources']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}
                stage('git-pull-5') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: "master"]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', 
                                                                        relativeTargetDir: 'kai-resource']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://admin@192.168.57.237:19000/KAI-MS/kai-web']]
                                                        ])}}
            }
        }
        stage('build'){
            parallel { 
                stage('build-1') {steps {build job: 'TestAutomation_KAI_M_WEB_DATABASE'}}
                stage('build-2') {steps {build job: 'TestAutomation_KAI_M_WEB_CSS_DL' , parameters: [booleanParam(name: 'do_compile', value: false)]}}
                stage('build-3') {steps {build job: 'TestAutomation_KAI_M_WEB_PYLON_MGR_JAR'}}
                stage('build-4') {steps {build job: 'TestAutomation_KAI_M_WEB_2_JAR'}}
                stage('build-5') {steps {build job: 'TestAutomation_KAI_M_WEB_CC_ADMIN'}}
                stage('build-6') {steps {build job: 'TestAutomation_KAI_M_WEB_COMMAND_CENTER'}}
                stage('build-7') {steps {build job: 'TestAutomation_KAI_M_WEB_PACKAGING_PIPE', 
                                        parameters: [string(name: 'cc_projects', 
                                                            value: 'cc-utils,cc-boot-dlrt,cc-admin-command,cc-extend-command,cc-keras-model,cc-protocol,cc-quartz-scheduler,cc-score-relay,cc-score-netty'),
                                                    booleanParam(name: 'build_all_subJOB', value: true),
                                                    booleanParam(name: 'containsTools', value: false)]
                                        }
                                }
                stage('build-8') {steps {build job: 'TestAutomation_KAI_M_WEB_COMMAND_CENTER'}}
            }
        }
	}
}





pipeline {
	agent {label "test-automation"}
	tools {
		gradle "Gradle 4.4"
	}
	stages {
        stage('git-pull'){
            parallel { 
                stage('git-pull-1') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: params.kai_m_sc_python_build_target]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'kai-package']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://192.168.57.237:19000/KAI-M/kai-m-sc-python']]
                                                        ])}}
                stage('git-pull-2') {steps {checkout([$class: 'SubversionSCM', 
                                                        additionalCredentials: [], 
                                                        excludedCommitMessages: '', 
                                                        excludedRegions: '', 
                                                        excludedRevprop: '', 
                                                        excludedUsers: '', 
                                                        filterChangelog: false, 
                                                        ignoreDirPropChanges: false, 
                                                        includedRegions: '', 
                                                        locations: [[cancelProcessOnExternalsFail: true, 
                                                                        credentialsId: 'admin', 
                                                                        depthOption: 'infinity', 
                                                                        ignoreExternalsOption: true, 
                                                                        local: './kaisc-package', 
                                                                        remote: 'http://192.168.57.237:19000/svn/KAI-M/deployment/kaisc-package']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}
            }
        }
        stage('shell-workspace'){
            steps{
                sh '''#!/bin/bash
                if [ -d "$WORKSPACE/kaisc-package-res" ]; then
                    rm -rf "$WORKSPACE/kaisc-package-res"
                fi

                mkdir "$WORKSPACE/kaisc-package-res"
                cp -r "$WORKSPACE/kaisc-package/." "$WORKSPACE/kaisc-package-res"
                chmod -R 777 "$WORKSPACE/kaisc-package-res"

                ##### remove .svn directories
                cd "$WORKSPACE/kaisc-package-res"
                find . -name .svn -exec rm -rf '{}' ;
                cd "$WORKSPACE"
                '''
            }
        }
        stage('conditional-build-python-lib'){
            when{
                expression{
                    return params.build_all_subJOB == 'true';
                }
            }
            steps{build job: 'TestAutomation_KAI_M_PYTHON_LIB'}
        }
        stage('conditional-copy'){
            when{
                expression{
                    return params.tools == 'linux-cpu' || params.tools == 'linux-gpu';
                }
            }
            steps{
                step ([$class: 'CopyArtifact',
                        projectName: 'TestAutomation_KAI_M_PYTHON_LIB',
                        filter: '*.tar.gz, *.txt',
                        fingerprint: true]);
            }
        }
        stage('shell-copy-tool'){
            steps{
                sh '''#!/bin/bash
                case "$tools" in
                    *linux*)
                        cp -r /data/jenkins/kai-kcbml-standalone/linux $WORKSPACE/kaisc-package-res/tools/install
                        
                        ##### jdk
                        echo "install linux openjdk jdk 1.8 version"
                        mkdir -p $WORKSPACE/kaisc-package-res/tools/jdk/linux
                        tar -zxvf "/home/ml/download/linux_jdk.tar.gz" -C $WORKSPACE/kaisc-package-res/tools/jdk/linux
                        
                        ##### python
                        mkdir -p $WORKSPACE/kaisc-package-res/tools/python
                        /bin/sh $WORKSPACE/kaisc-package-res/tools/install/Miniconda3-4.7.12-Linux-x86_64.sh -f -b -p $WORKSPACE/kaisc-package-res/tools/python
                        
                        if [ "$tools" = "linux-cpu" ]; then
                            echo "install linux python 3.7 for using cpu environment"
                            tar -zxvf "$WORKSPACE/python_pkgs_cpu.tar.gz" -C $WORKSPACE/kaisc-package-res/tools/install
                            $WORKSPACE/kaisc-package-res/tools/python/bin/pip install -r $WORKSPACE/kai_m_requirement_cpu.txt --no-index --find-links=$WORKSPACE/kaisc-package-res/tools/install/python_pkgs_cpu
                        elif [ "$tools" = "linux-gpu" ]; then
                            echo "install linux python 3.7 for using gpu environment"
                            tar -zxvf "$WORKSPACE/python_pkgs.tar.gz" -C $WORKSPACE/kaisc-package-res/tools/install
                            $WORKSPACE/kaisc-package-res/tools/python/bin/pip install -r $WORKSPACE/kai_m_requirement.txt --no-index --find-links=$WORKSPACE/kaisc-package-res/tools/install/python_pkgs
                        else
                            echo "invalid 'tools' parameter"
                        fi
                        
                        ##### delete install directory
                        rm -rf $WORKSPACE/kaisc-package-res/tools/install
                    ;;
                    *windows*)
                        ##### jdk
                        echo "install windows zulu jdk 1.8 version"
                        unzip "/home/ml/download/jenkins/kai-kcbml-standalone/zulu_jdk.zip" -d "$WORKSPACE/kaisc-package-res/tools"
                        
                        ##### python
                        mkdir -p $WORKSPACE/kaisc-package-res/tools/python
                        unzip "/home/ml/download/jenkins/kai-kcbml-standalone/python-3.7.4-embed-amd64-sc.zip" -d "$WORKSPACE/kaisc-package-res/tools/python"
                        
                        ##### site-packages
                        if [ "$tools" = "windows-cpu" ]; then
                            echo "install linux python 3.7 for using cpu environment"
                            mkdir -p $WORKSPACE/kaisc-package-res/tools/site-packages
                            tar xzf "/home/ml/download/jenkins/kai-kcbml-standalone/site-packages-3.7-cpu.tgz" -C "$WORKSPACE/kaisc-package-res/tools/site-packages"
                        elif [ "$tools" = "windows-gpu" ]; then
                            echo "install linux python 3.7 for using gpu environment"
                            mkdir -p $WORKSPACE/kaisc-package-res/tools/site-packages
                            tar xzf "/home/ml/download/jenkins/kai-kcbml-standalone/site-packages-3.7-gpu.tgz" -C "$WORKSPACE/kaisc-package-res/tools/site-packages"
                        else
                            echo "invalid 'tools' parameter"
                        fi
                    ;;
                esac
                '''
            }
        }
        stage('conditional-build'){
            when{
                expression{
                    return params.build_all_subJOB == true;
                }
            }
            steps{
                build job: 'TestAutomation_KAI_M_WEB_DATABASE'
                build job: 'TestAutomation_CC_SCORE_JAR'
                build job: 'TestAutomation_KAI_M_WEB_CC_ADMIN_SHELL'
                build job: 'TestAutomation_CC_SHELL3_JAR'
                build job: 'TestAutomation_KAI_M_SC_PYTHON_PACKAGING'
            }
        }
        stage('shell-post-1'){
            steps{
                sh '''#!/bin/bash
                ####### 0003.CommandCenter-Score to JAR
                cp "$WORKSPACE/../TestAutomation_CC_SCORE_JAR/cc-master/cc-boot-score/build/libs/cc-boot-score-1.2.0.jar" "$WORKSPACE/kaisc-package-res/libs/cc-boot-score-1.2.0.jar"

                ####### 0009.CommandCenter cc-score-shell to JAR
                cp "$WORKSPACE/../TestAutomation_CC_SHELL3_JAR/cc-shell/cc-score-shell/build/libs/cc-score-shell-2.0.0.jar" "$WORKSPACE/kaisc-package-res/libs/cc-score-shell-2.0.0.jar"

                ####### 0010.CommandCenter cc-admin-shell to JAR
                cp "$WORKSPACE/../TestAutomation_KAI_M_WEB_CC_ADMIN_SHELL/cc-shell/cc-admin-shell/build/libs/cc-admin-shell-2.0.0.jar" "$WORKSPACE/kaisc-package-res/libs/cc-admin-shell-2.0.0.jar"

                ####### 9004.KAI-M-SC-PYTHON-PACKAGING
                cp -r "$WORKSPACE/../TestAutomation_KAI_M_SC_PYTHON_PACKAGING/score" "$WORKSPACE/kaisc-package-res/backend"

                ####### DATABASE COPY
                cp -r "$WORKSPACE/../TestAutomation_KAI_M_WEB_DATABASE/package-database/database/cc/." "$WORKSPACE/kaisc-package-res/data"
                # cp -r "$WORKSPACE/../TestAutomation_KAI_M_WEB_DATABASE/package-database/database/kaisc/." "$WORKSPACE/kaisc-package-res/data"
                '''
            }
        }
        stage('shell-post-2'){
            steps{
                sh '''#!/bin/bash
                ###### remove package-database
                rm -rf "$WORKSPACE/kaisc-package-res/package-database"

                if [ -d "$WORKSPACE/temp" ]; then
                    rm -rf "$WORKSPACE/temp"
                fi
                mkdir "$WORKSPACE/temp"

                cd "$WORKSPACE/kaisc-package-res"
                tar -czvf ../temp/kaisc-package-$(date +%Y%m%d).tar.gz *
                '''
            }
        }
	}
    post{
        success{
            archiveArtifacts artifacts: 'temp/kaisc-package*.tar.gz'
        }
    }
}




pipeline {
	agent {label "test-automation"}
	stages{
        stage('Ready') { 
            cleanWs()
            checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '55cf9ef0-a928-45d3-8786-ffdee61ac973', url: 'http://192.168.57.237:19000/KAI-M/kai-m-python-library']]])
        }
    
        stage('Build') {
            sh label: '', script: './build.sh '+ params.cuda_version + ' ' + params.python_version
        }
        archiveArtifacts artifacts: '*.tar.gz', fingerprint: true
        archiveArtifacts artifacts: '*.txt', fingerprint: true
	}
}








