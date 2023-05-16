/*
-----------------------------------------------------------
Kai-M Solution's Test Automation Environment Script
Made by stuko
Modified : 2022-07-07
-----------------------------------------------------------
*/

pipeline {
    agent {label "test-automation"}
    environment { 
        JAVA_HOME_LOCAL='$WORKSPACE/kai-download/tools/jdk/linux'
    }
    options{
        timeout(6000000)
    }
    triggers {
        cron('H 6 * * *')
    }
    parameters {
        string(name: 'python_version', defaultValue: '3.8', description: 'library python version')
        string(name: 'cuda_version', defaultValue: '10.1', description: 'cuda version for determine tensorflow version')
        string(name: 'build_all_subJOB', defaultValue: 'true', description: '')
        string(name: 'tools', defaultValue: 'linux-cpu', description: '')
        string(name: 'kai_m_sc_python_build_target', defaultValue: 'any', description: '')
        string(name: 'kai_web_version', defaultValue: '2.0.0', description: '')
        string(name: 'cc_admin_version', defaultValue: '1.1.0', description: '')
        string(name: 'cc_boot_version', defaultValue: '1.0.0', description: '')
        string(name: 'cc_boot_score_version', defaultValue: '1.2.0', description: '')
        string(name: 'cc_admin_shell_version', defaultValue: '2.0.0', description: '')
        string(name: 'cc_shell_score_version', defaultValue: '2.0.0', description: '')
        string(name: 'h2_db_version', defaultValue: '1.4.193', description: '')
        string(name: 'build_project', defaultValue: 'cc-utils,cc-boot,cc-security,cc-admin-command,cc-extend-command,cc-quartz-scheduler,cc-extend-utils', description: '')
        booleanParam(name: 'make_docker_image', defaultValue: false, description: '')
        booleanParam(name: 'do_compile', defaultValue: false, description: '')
        text(name: 'DESC', defaultValue: '', description: 'Description')
        booleanParam(name: 'SKIP', defaultValue: false, description: 'Skip Step')
        choice(name: 'CHOICE', choices: ['Git', 'Svn'], description: 'Select remote repository')
    }
    tools {
        gradle "Gradle 4.4"
    }
	stages {
        stage('git-global-config'){
            steps{
                sh '''#!/bin/bash
				git config --global http.postBuffer 524288000
				git config --global https.postBuffer 524288000
				git config --global http.maxRequestBuffer 524288000
				git config --global https.maxRequestBuffer 524288000
				git config --global core.compression 0

				git config --global ssh.postBuffer 2048M
				git config --global ssh.maxRequestBuffer 1024M

				git config --global pack.windowMemory 256m 
				git config --global pack.packSizeLimit 256m
                '''
            }
        }

        stage('pull-source-repository'){
            parallel {
                stage('svn-pull-kai-sc-database') {steps {checkout([$class: 'SubversionSCM', 
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
                                                                        local: './kai-sc-database', 
                                                                        remote: 'http://192.168.57.237:19000/svn/KAI-M/deployment/kaisc-package/package-database']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}
                stage('git-pull-kai-ms-deployment') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: "master"]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'kai-package']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://admin@192.168.57.237:19000/KAI-MS/deployment']]
                                                        ])}}
                stage('git-pull-kai-m-common-python') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: "master"]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'kai-m-common-python']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://admin@192.168.57.237:19000/KAI-M/kai-m-common-python']]
                                                        ])}}
                stage('svn-pull-pylon-framework-manager-webapp') {steps {checkout([$class: 'SubversionSCM', 
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
                stage('svn-pull-pylon-action-logger') {steps {checkout([$class: 'SubversionSCM', 
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
                stage('svn-pull-cc-admin-webapp') {steps {checkout([$class: 'SubversionSCM', 
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
                stage('git-pull-kai-web') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: "master"]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', 
                                                                        relativeTargetDir: 'kai-web']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://admin@192.168.57.237:19000/KAI-MS/kai-web']]
                                                        ])}}
                /*                                        
                stage('git-pull-kai-download') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: "master"]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory',relativeTargetDir: 'kai-download'],
                                                                     [$class: 'CloneOption', timeout: 6000000]], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://admin@192.168.57.237:19000/admin/kai-download']]
                                                        ])}}  
                */
                stage('svn-pull-pylon-framework') {steps {checkout([$class: 'SubversionSCM', 
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
                                                                        local: './pylon-framework', 
                                                                        remote: 'http://192.168.57.237:19000/svn/COMMON/pylon-framework/branches/v2.0.0/pylon-framework']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}
                stage('svn-pull-dl-css') {steps {checkout([$class: 'SubversionSCM', 
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
                                                                        local: './dl-css', 
                                                                        remote: 'http://192.168.57.237:19000/svn/KAI-M/kai-m-web/trunk/kai-m-web']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}                                                        
                stage('svn-pull-cc-admins') {steps {checkout([$class: 'SubversionSCM', 
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
                                                                        local: './cc-admins', 
                                                                        remote: 'http://192.168.57.237:19000/svn/COMMON/commandCenter/cc-admins']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}   
                stage('svn-pull-cc-master') {steps {checkout([$class: 'SubversionSCM', 
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
                                                                        local: './cc-master', 
                                                                        remote: 'http://192.168.57.237:19000/svn/COMMON/commandCenter/cc-master']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}                                                           
                stage('svn-pull-cc-shell') {steps {checkout([$class: 'SubversionSCM', 
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
                                                                        local: './cc-shell', 
                                                                        remote: 'http://192.168.57.237:19000/svn/COMMON/commandCenter/cc-shell']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}       
                                                        
                stage('git-pull-kai-m-sc-python') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: 'master']], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'kai-m-sc-python']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://192.168.57.237:19000/KAI-M/kai-m-sc-python']]
                                                        ])}}
                stage('svn-pull-kaisc-package') {steps {checkout([$class: 'SubversionSCM', 
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
               stage('git-pull-cc-service-database') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: "master"]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: './cc-service-database']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://admin@192.168.57.237:19000/KAI-MS/kai-commandCenter-Service']]
                                                        ])}}                                                        
                stage('svn-pull-pylon-framework-database') {steps {checkout([$class: 'SubversionSCM', 
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
                                                                        local: './pylon-framework-database', 
                                                                        remote: 'http://192.168.57.237:19000/svn/COMMON/pylon-framework/pylon-framework-database']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}                                                        
                stage('svn-pull-cc-admin-deployment-package-database') {steps {checkout([$class: 'SubversionSCM', 
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
                                                                        local: './cc-admin-deployment-package-database', 
                                                                        remote: 'http://192.168.57.237:19000/svn/COMMON/cc-admin-deployment/package-database']], 
                                                        quietOperation: true, 
                                                        workspaceUpdater: [$class: 'UpdateUpdater']])}}                                                                 
               stage('git-pull-kai-m-python') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: "master"]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: './kai-m-python']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://admin@192.168.57.237:19000/KAI-M/kai-m-python']]
                                                        ])}}                                                        
               stage('git-pull-kai-m-python-library') {steps {checkout([$class: 'GitSCM', 
                                                        branches: [[name: "master"]], 
                                                        doGenerateSubmoduleConfigurations: false, 
                                                        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: './kai-m-python-library']], 
                                                        submoduleCfg: [], 
                                                        userRemoteConfigs: [[credentialsId: 'admin', 
                                                                            url: 'http://192.168.57.237:19000/KAI-M/kai-m-python-library']]
                                                        ])}}                                                        
            }
        }
        stage('shell-jdk'){
            steps{
                sh '''#!/bin/bash
                mkdir -p kai-download/tools/jdk/linux
                tar -xzf $WORKSPACE/kai-download/linux_jdk.tar.gz -C $WORKSPACE/kai-download/tools/jdk/linux/
                ln -sfn $WORKSPACE/kai-download/tools/jdk/linux ~/java_home
                JAVA_HOME="~/java_home"
                '''
            }
        }
        stage('shell-pylon-framework-database'){
            steps{
                sh '''#!/bin/bash
                chmod -R 777 "$WORKSPACE/pylon-framework-database"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$WORKSPACE/pylon-framework-database/bin/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$WORKSPACE/pylon-framework-database/database/pylon;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$WORKSPACE/pylon-framework-database/sql/frontend-pylon-framework-clear.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$WORKSPACE/pylon-framework-database/bin/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$WORKSPACE/pylon-framework-database/database/pylon;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$WORKSPACE/pylon-framework-database/sql/frontend-pylon-framework-base.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$WORKSPACE/pylon-framework-database/bin/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$WORKSPACE/pylon-framework-database/database/pylon;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$WORKSPACE/pylon-framework-database/sql/frontend-pylon-framework-extends.sql"
                '''
            }
            post{
                success{
                    archiveArtifacts artifacts: 'pylon-framework-database/database/**/*.db'
                }
            }
        } 
        stage('shell-cc-admin-deployment-package-database'){
            steps{
                sh '''#!/bin/bash
                chmod -R 777 "$WORKSPACE/cc-admin-deployment-package-database"
                cp -r "$WORKSPACE/pylon-framework-database/." "$WORKSPACE/cc-admin-deployment-package-database"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$WORKSPACE/cc-admin-deployment-package-database/bin/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$WORKSPACE/cc-admin-deployment-package-database/database/pylon;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$WORKSPACE/cc-admin-deployment-package-database/sql/frontend-pylon-framework-clear.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$WORKSPACE/cc-admin-deployment-package-database/bin/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$WORKSPACE/cc-admin-deployment-package-database/database/pylon;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$WORKSPACE/cc-admin-deployment-package-database/sql/frontend-pylon-framework-base.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$WORKSPACE/cc-admin-deployment-package-database/bin/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$WORKSPACE/cc-admin-deployment-package-database/database/pylon;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$WORKSPACE/cc-admin-deployment-package-database/sql/frontend-pylon-framework-extends.sql"

                cp "$WORKSPACE/cc-admin-deployment-package-database/database/pylon.mv.db" "$WORKSPACE/cc-admin-deployment-package-database/database/test.mv.db"
                # sh "$WORKSPACE/cc-admin-deployment-package-database/bin/frontend-cc-admin.sh"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$WORKSPACE/cc-admin-deployment-package-database/bin/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$WORKSPACE/cc-admin-deployment-package-database/database/test" -user "sa" -password "1234!" -script "$WORKSPACE/cc-admin-deployment-package-database/sql/frontend-pylon-framework-extends-ccadmin.sql"

                # rm -rf "$WORKSPACE/cc-admin-deployment-package-database/database/pylon.mv.db"
                '''
            }
            post{
                success{
                    archiveArtifacts artifacts: 'cc-admin-deployment-package-database/database/**/*.db'
                }
            }
        } 
        stage('shell-cc-service-database'){
            steps{
                sh '''#!/bin/bash
                chmod -R 777 "$WORKSPACE/cc-service-database"
                ###############################################################
                # 
                # pylon database copy
                # 
                ###############################################################
                mkdir "$WORKSPACE/cc-service-database/database/pylon"
                cp "$WORKSPACE/cc-admin-deployment-package-database/database/pylon.mv.db" "$WORKSPACE/cc-service-database/database/pylon/pylon.mv.db"
                ###############################################################
                # 
                # default pylon database copy
                # 
                ###############################################################
                mkdir "$WORKSPACE/cc-service-database/database/ml-web"
                cp "$WORKSPACE/cc-admin-deployment-package-database/database/pylon.mv.db" "$WORKSPACE/cc-service-database/database/ml-web/kai.mv.db"
                # rm -rf "$WORKSPACE/cc-admin-deployment-package-database/database/pylon.mv.db"

                mkdir "$WORKSPACE/cc-service-database/database/cc-admin"
                cp "$WORKSPACE/cc-admin-deployment-package-database/database/test.mv.db" "$WORKSPACE/cc-service-database/database/cc-admin/test.mv.db"
                # rm -rf "$WORKSPACE/cc-admin-deployment-package-database/database/test.mv.db"

                cd "$WORKSPACE/cc-service-database"
                find . -name .git -exec rm -rf '{}' \\;
                cd "$WORKSPACE"

                echo "--------------------------------------------------"
                # echo "make.cc-commandCenter database information"
                # sh "$WORKSPACE/cc-service-database/bin/make.cc-commandCenter.sh"

                BASEDIR="$WORKSPACE/cc-service-database/bin"
                cd "$BASEDIR"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-default.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/dbs-i18n.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-system.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-function.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-help.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-i18n.sql"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc-hist;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-history.sql"



                echo "--------------------------------------------------"
                # echo "make.cc-commandCenter-algorithms database information"
                # sh "$WORKSPACE/cc-service-database/bin/make.cc-commandCenter-algorithms.sh"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-common.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-auto-encoder-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-cnn.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-decision-tree-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-dnn.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-ensemble-model-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-light-gbm-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-logistic-regression-score-cpu.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-random-forest-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-xgboost-score.sql"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-cnn-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-decision-tree-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-dnn-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-dtree-stg.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-kcbml.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-light-gbm-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-logistic-regression-legacy.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-meta-anova.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-random-forest-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-xgboost-css-score.sql"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-generalized-linear-model-regression.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-kcb-synthetic-model-generation.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-light-gbm-regression.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-xgboost-regression.sql"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-operator2.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-scheduler.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/dbs/ml;PASSWORD_HASH=TRUE" -user "sa" -password "1c62334132775580b91a18c17750e2847f55373079e72f71a692cd939b17131d" -script "$BASEDIR/../sql/dbs-service-scheduler.sql"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-common.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-csscommon.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-dlcommon.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-legacycommon.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-regressioncommon.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-auto-encoder-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-cnn.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-decision-tree-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-dnn.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-ensemble-model-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-light-gbm-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-logistic-regression-score-cpu.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-random-forest-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-xgboost-score.sql"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-cnn-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-decision-tree-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-dnn-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-dtree-stg.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-kcbml.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-light-gbm-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-logistic-regression-legacy.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-meta-anova.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-random-forest-css-score.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-xgboost-css-score.sql"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-generalized-linear-model-regression.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-kcb-synthetic-model-generation.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-light-gbm-regression.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-algorithm-xgboost-regression.sql"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-operator2.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-scheduler.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script "$BASEDIR/../sql/cc-service-scheduler.sql"



                echo "--------------------------------------------------"
                # echo "make.web-kaim database information"
                # sh "$WORKSPACE/cc-service-database/bin/make.web-kaim.sh"

                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/ml-web/kai;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$BASEDIR/../sql/frontend-pylon-framework-extends-kaim.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/ml-web/kai;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$BASEDIR/../sql/frontend-operator2.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/ml-web/kai;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$BASEDIR/../sql/frontend-scheduler.sql"
                "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/ml-web/kai;PASSWORD_HASH=TRUE" -user "sa" -password "fa68502b1fc40b8dafae16d15227e309613048403dd9f069b0dd148fefdf608b" -script "$BASEDIR/../sql/frontend-service-scheduler.sql"

                echo "--------------------------------------------------"


                cd "$WORKSPACE/cc-service-database/bin"

                JAVA_EXEC=$WORKSPACE/kai-download/tools/jdk/linux/bin/java
                echo "--------------------------------------------------"

                echo JAVA_EXEC: $JAVA_EXEC
                $JAVA_EXEC -jar cc-builder-$cc_boot_version.jar -jdbc "jdbc:h2:$WORKSPACE/cc-service-database/database/cc/cc;PASSWORD_HASH=TRUE" -username "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -v


                '''
            }
            post{
                success{
                    archiveArtifacts artifacts: 'cc-service-database/database/**/*.mv.db'
                }
            }
        }        
        stage('build'){
            stages { 
                stage('shell-kai-m-python'){
                    steps{
                        sh '''#!/bin/bash
                        cd "$WORKSPACE/kai-m-python"
                        # do_compile option = false
                        # ./deploy.sh -c $do_compile

                        cp -rf $WORKSPACE/kai-m-common-python/model $WORKSPACE/kai-m-python/common/
                        cp -rf $WORKSPACE/kai-m-common-python/* $WORKSPACE/kai-m-python/common/

                        usage() { echo "Usage: $0 [-c <true|false>] [-d <string>]" 1>&2; exit 1; }
                        while getopts "c:d:h" opt
                        do
                            case $opt in
                                c) is_compile=$OPTARG
                                echo "Arg C: $is_compile"
                                ;;
                                d) docker_image=$OPTARG
                                echo "Arg D: $docker_image"
                                ;;
                                h) usage ;;
                                ?) usage ;;
                            esac
                        done

                        if [ -z "$is_compile" ]; then
                                echo "is_compile = ${is_compile}"
                        fi

                        if [ "${docker_image}" ]; then
                            echo "docker_image = ${docker_image}"
                        fi

                        home_dir=$(pwd)

                        find ./ -name '__pycache__' | xargs rm -rf
                        find ./ -name '*.pyc' | xargs rm -rf
                        rm -rf "$home_dir"/deploy
                        ls "$home_dir"

                        dirs=$(find ./ -type d | grep -v "\\.git" | grep -v './test')
                        if [ "$is_compile" = "true" ]; then
                        python3.8 -m compileall ./
                        fi
                        if [ ! -d "$home_dir"/deploy ]; then
                        mkdir "$home_dir"/deploy
                        fi

                        for item in $dirs
                        do
                        if [ ! -d "$home_dir"/deploy/$item ]; then
                            mkdir -p "$home_dir"/deploy/$item
                        fi

                        if [ -d "$home_dir"/$item/__pycache__ ]; then
                            cd "$home_dir"/$item/__pycache__
                            ls -1 | sed "s/\\(.*\\).cpython-38.pyc$/mv '&' '\\1.pyc' /" | sh
                            cp ./*.p* "$home_dir"/deploy/$item/
                        else
                            echo "$home_dir"/$item
                            cd "$home_dir"/$item
                            cp ./* "$home_dir"/deploy/$item/
                        fi
                        done

                        if [ "$is_compile" != "true" ]; then
                            # rm "$home_dir"/deploy/ML_RUN_PROCESS.py
                            python3.8 -m py_compile "$home_dir"/ML_RUN_PROCESS.py
                            mv "$home_dir"/__pycache__/ML_RUN_PROCESS.*.pyc "$home_dir"/deploy/ML_RUN_PROCESS.pyc
                        fi
                        cp  "$home_dir"/test/test_process.py  "$home_dir"/deploy/test_process.py
                        rm "$home_dir"/deploy/*.sh
                        mkdir -p "$home_dir"/deploy/tmp
                        mkdir -p "$home_dir"/deploy/log

                        if [ "${docker_image}" ]; then
                            cd "$home_dir"/deploy
                            sudo docker cp ./ $(sudo docker ps -a | grep $1:latest | awk '{print $1}'):/kcbml/backend/mlCSS/
                        fi

                        cd "$WORKSPACE/kai-m-python/deploy"
                        zip -r mlCSS.zip ./*
                        '''
                    }
                    post{
                        success{
                            archiveArtifacts artifacts: 'kai-m-python/deploy/mlCSS.zip'
                        }
                    }
                }
                stage('pull-build-pylon-kai-web-cc'){
                    parallel {       
                        stage('build-pylon-framework') {
                            steps {
                                sh '''#!/bin/bash
                                cd $WORKSPACE/pylon-framework
                                chmod a+x ./gradlew
                                ./gradlew clean
                                ./gradlew build --refresh-dependencies
                                ./gradlew jar
                                ./gradlew bootJar
                                '''
                            }
                            post{
                                success{
                                    archiveArtifacts artifacts: 'pylon-framework/pylon-framework-manager-webapp/build/libs/pylon-framework-manager-webapp-*.jar'
                                }
                            }
                        }
                        stage('build-kai-web') {
                            steps {
                                sh '''#!/bin/bash
                                cd $WORKSPACE/kai-web
                                chmod a+x ./gradlew
                                ./gradlew clean
                                ./gradlew jar --refresh-dependencies
                                ./gradlew build --refresh-dependencies
                                ./gradlew bootJar
                                '''
                            }
                            post{
                                success{
                                    archiveArtifacts artifacts: 'kai-web/kai-common-webapp/build/libs/kai-common-webapp-'+params.kai_web_version+'-SNAPSHOT.jar'
                                }
                            }
                        }
                        stage('build-cc-admins') {
                            steps {
                                sh '''#!/bin/bash
                                cd $WORKSPACE/cc-admins
                                chmod a+x ./gradlew
                                ./gradlew clean
                                ./gradlew build --refresh-dependencies
                                ./gradlew jar
                                ./gradlew bootRepackage
                                '''
                            }
                            post{
                                success{
                                    archiveArtifacts artifacts: 'cc-admins/cc-admin-webapp/build/libs/cc-admin-webapp-'+params.cc_admin_version+'-SNAPSHOT.jar'
                                }
                            }
                        }
                        stage('build-cc-master') {
                            steps {
                                sh '''#!/bin/bash
                                cd $WORKSPACE/cc-master
                                chmod a+x ./gradlew
                                ./gradlew clean
                                ./gradlew build --refresh-dependencies -Dprojects=$build_project
                                ./gradlew jar -Dprojects=$build_project
                                ./gradlew bootRepackage -Dprojects=$build_project
                                '''
                            }
                            post{
                                success{
                                    archiveArtifacts artifacts: 'cc-master/cc-boot/build/libs/cc-boot-'+params.cc_boot_version+'.jar'
                                }
                            }
                        }
                    }
                }
                stage('build-kai-sc-package-python') {
                    steps {
                        sh '''#!/bin/bash
                        #########################################################################################################
                        #
                        # project copying
                        #
                        #########################################################################################################
                        if [ -d "$WORKSPACE/kaisc-package-res" ]; then
                            rm -rf "$WORKSPACE/kaisc-package-res"
                        fi

                        mkdir "$WORKSPACE/kaisc-package-res"
                        cp -r "$WORKSPACE/kaisc-package/." "$WORKSPACE/kaisc-package-res"
                        chmod -R 777 "$WORKSPACE/kaisc-package-res"

                        ##### remove .svn directories
                        cd "$WORKSPACE/kaisc-package-res"
                        find . -name .svn -exec rm -rf '{}' \\;
                        cd "$WORKSPACE"
                        '''
                    }
                }
                stage('build-python-lib'){
                    steps{
                        // cleanWs()
                        sh '''#!/bin/bash
                        cd "$WORKSPACE/kai-m-python-library"
                        ./build.sh $cuda_version $python_version
                        '''                                
                        archiveArtifacts artifacts: 'kai-m-python-library/*.tar.gz', fingerprint: true
                        archiveArtifacts artifacts: 'kai-m-python-library/*.txt', fingerprint: true
                    }
                }
                stage('shell-copy-tool'){
                    steps{
                        sh '''#!/bin/bash
                        case "$tools" in
                            *linux*)
                                # Not exist folder install
                                mkdir -p $WORKSPACE/kaisc-package-res/tools/install
                                cp -r $WORKSPACE/kai-download/tools/jdk/linux $WORKSPACE/kaisc-package-res/tools/install
                                
                                ##### jdk
                                echo "install linux openjdk jdk 1.8 version"
                                mkdir -p $WORKSPACE/kaisc-package-res/tools/jdk/linux
                                tar -zxvf "$WORKSPACE/kai-download/linux_jdk.tar.gz" -C $WORKSPACE/kaisc-package-res/tools/jdk/linux
                                
                                ##### python
                                mkdir -p $WORKSPACE/kaisc-package-res/tools/python
                                # cp "$WORKSPACE/kai-download/Miniconda3-4.7.12-Linux-x86_64.sh" $WORKSPACE/kaisc-package-res/tools/python/
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
                                unzip "$WORKSPACE/kai-download/zulu_jdk.zip" -d "$WORKSPACE/kaisc-package-res/tools"
                                
                                ##### python
                                mkdir -p $WORKSPACE/kaisc-package-res/tools/python
                                unzip "$WORKSPACE/kai-download/python-3.7.4-embed-amd64-sc.zip" -d "$WORKSPACE/kaisc-package-res/tools/python"
                                
                                ##### site-packages
                                if [ "$tools" = "windows-cpu" ]; then
                                    echo "install linux python 3.7 for using cpu environment"
                                    mkdir -p $WORKSPACE/kaisc-package-res/tools/site-packages
                                    tar xzf "$WORKSPACE/kai-download/site-packages-3.7-cpu.tgz" -C "$WORKSPACE/kaisc-package-res/tools/site-packages"
                                elif [ "$tools" = "windows-gpu" ]; then
                                    echo "install linux python 3.7 for using gpu environment"
                                    mkdir -p $WORKSPACE/kaisc-package-res/tools/site-packages
                                    tar xzf "$WORKSPACE/kai-download/site-packages-3.7-gpu.tgz" -C "$WORKSPACE/kaisc-package-res/tools/site-packages"
                                else
                                    echo "invalid 'tools' parameter"
                                fi
                            ;;
                        esac
                        '''
                    }
                }
                stage('shell-kai-sc-database'){
                    steps{
                        sh '''#!/bin/bash
                        echo "--------------------------------------------------"
                        echo "make-default database information"
                        chmod -R 777 "$WORKSPACE/kai-sc-database"
                        cd "$WORKSPACE/kai-sc-database/bin"
                        ./make.sh
                        '''
                    }
                }
                stage('pull-build-cc-score-shell-'){
                    stages {  
                        stage('build-cc-master-score') {
                            steps {
                                sh '''#!/bin/bash
                                cd $WORKSPACE/cc-master
                                chmod a+x ./gradlew
                                ./gradlew clean
                                ./gradlew jar --refresh-dependencies 
                                ./gradlew bootRepackage
                                '''
                            }
                            post{
                                success{
                                    archiveArtifacts artifacts: 'cc-master/cc-boot-score/build/libs/cc-boot-score-' + params.cc_boot_score_version + '.jar'
                                }
                            }
                        }
                        stage('build-cc-shell') {
                            steps {
                                sh '''#!/bin/bash
                                cd $WORKSPACE/cc-shell
                                # chmod a+x ./gradlew
                                ../cc-admins/gradlew clean
                                ../cc-admins/gradlew jar --refresh-dependencies 
                                ../cc-admins/gradlew bootRepackage
                                '''
                            }
                            post{
                                success{
                                    archiveArtifacts artifacts: 'cc-shell/cc-admin-shell/build/libs/cc-admin-shell-' +params.cc_admin_shell_version+'.jar'
                                }
                            }
                        }
                        stage('build-cc-shell-score') {
                            steps {
                                sh '''#!/bin/bash
                                cd $WORKSPACE/cc-shell
                                # chmod a+x ./gradlew
                                ../cc-admins/gradlew clean
                                ../cc-admins/gradlew jar --refresh-dependencies 
                                ../cc-admins/gradlew bootRepackage
                                '''
                            }
                            post{
                                success{
                                    archiveArtifacts artifacts: 'cc-shell/cc-score-shell/build/libs/cc-score-shell-'+params.cc_shell_score_version+'.jar'
                                }
                            }
                        }
                    }
                }
                stage('build-kai-m-sc-python') {
                    steps {
                        sh '''#!/bin/bash
                        if [ -d "$WORKSPACE/kai-m-sc-python/deploy" ]; then
                            rm -rf "$WORKSPACE/kai-m-sc-python/deploy"
                        fi

                        mkdir -p "$WORKSPACE/kai-m-sc-python/deploy"
                        cd $WORKSPACE/kai-m-sc-python/deploy

                        zip -r score.zip ../score
                        '''
                    }
                    post{
                        success{
                            archiveArtifacts artifacts: 'kai-m-sc-python/deploy/score.zip'
                        }
                    }
                }
                stage('shell-copy-to-kaisc-package-res') {
                    steps {
                        sh '''#!/bin/bash
                        cp "$WORKSPACE/cc-master/cc-boot-score/build/libs/cc-boot-score-$cc_boot_score_version.jar" "$WORKSPACE/kaisc-package-res/libs/cc-boot-score-$cc_boot_score_version.jar"
                        cp "$WORKSPACE/cc-shell/cc-score-shell/build/libs/cc-score-shell-$cc_shell_score_version.jar" "$WORKSPACE/kaisc-package-res/libs/cc-score-shell-$cc_shell_score_version.jar"
                        cp "$WORKSPACE/cc-shell/cc-admin-shell/build/libs/cc-admin-shell-$cc_admin_shell_version.jar" "$WORKSPACE/kaisc-package-res/libs/cc-admin-shell-$cc_admin_shell_version.jar"
                        cp -r "$WORKSPACE/kai-m-sc-python/score" "$WORKSPACE/kaisc-package-res/backend"

                        cp -r "$WORKSPACE/cc-service-database/database/cc/." "$WORKSPACE/kaisc-package-res/data"
                        cp -r "$WORKSPACE/kai-sc-database/database/kaisc/." "$WORKSPACE/kaisc-package-res/data"
                        '''
                    }
                }
                stage('shell-tar-kaisc-package') {
                    steps {
                        sh '''#!/bin/bash
                        rm -rf "$WORKSPACE/kaisc-package-res/package-database"
                        if [ -d "$WORKSPACE/temp" ]; then
                            rm -rf "$WORKSPACE/temp"
                        fi
                        mkdir "$WORKSPACE/temp"
                        cd "$WORKSPACE/kaisc-package-res"
                        tar -czvf "$WORKSPACE/temp/kaisc-package-$(date +%Y%m%d).tar.gz" *
                        '''
                    }
                    post{
                        success{
                            archiveArtifacts artifacts: 'temp/kaisc-package*.tar.gz'
                        }
                    }
                }

                stage('shell-make-version') {
                    steps {
                        sh '''#!/bin/bash
                        JAVA_HOME="$WORKSPACE/kai-download/tools/jdk/linux"
                        PACKAGE_HOME="$WORKSPACE/kai-m-standalone-package"

                        KAI_DATABASE_PATH="$WORKSPACE/cc-service-database"
                        MLCSS_PATH="$WORKSPACE/kai-m-python"

                        if [ -d "$PACKAGE_HOME" ]; then
                            rm -rf "$PACKAGE_HOME"
                        fi

                        mkdir "$PACKAGE_HOME"
                        cp -r "$WORKSPACE/kai-package/." "$PACKAGE_HOME"
                        ###############################################################
                        # 
                        # database copying
                        # 
                        ###############################################################
                        
                        cp -r "$KAI_DATABASE_PATH" "$PACKAGE_HOME"
                        mv "$PACKAGE_HOME/cc-service-database" "$PACKAGE_HOME/package-database"

                        chmod -R 777 "$PACKAGE_HOME"

                        # remove .svn directories
                        cd "$PACKAGE_HOME"
                        find . -name .svn -exec rm -rf '{}' \\;
                        find . -name .git -exec rm -rf '{}' \\;
                        find . -name .keep -exec rm -rf '{}' \\;
                        cd "$WORKSPACE"

                        echo "==============================================="
                        echo "git reivision:" $GIT_COMMIT
                        echo "build number:" $BUILD_NUMBER
                        echo "==============================================="

                        ###############################################################
                        # 
                        # make database version creating
                        sh "$PACKAGE_HOME/package-database/bin/cc-build-make-version.sh" "cc-service-database" "cc-service-database" "$cc_boot_version-"$GIT_COMMIT"-"$BUILD_NUMBER "$PACKAGE_HOME/package-database/sql/cc-build-make-version.sql"
                        sh "$PACKAGE_HOME/package-database/bin/cc-build-make-version.sh" "kai-m-package-type" "kai-m-package-type" "cpu-version" "$PACKAGE_HOME/package-database/sql/cc-build-make-version.sql"
                        # sh "$PACKAGE_HOME/package-database/bin/cc-build-make-git-version.sh" "$MLCSS_PATH" "kai_python_engine" "kai_python_engine"
                        
                        BASEDIR="$PACKAGE_HOME/package-database/bin"
                        git_dir="$MLCSS_PATH"
                        module_id="kai_python_engine"
                        module_name="kai_python_engine"
                        cd "$git_dir"
                        git_timestamp=$(git show -s --format=%ct HEAD)
                        git_rev=$(git rev-parse HEAD)
                        commit_date=$(date -d @$git_timestamp +%Y%m%d%H%M%S)
                        cd $OLDPWD
                        build_date=$(date +%Y%m%d%H%M%S)
                        sql="INSERT INTO PUBLIC.TB_CC_MODULE_INFO (MODULE_ID, MODULE_NM, MODULE_VER, MODULE_CREATE_DTM, REG_ID, REG_DTM, MOD_ID, MOD_DTM) VALUES('$2', '$3', '$git_rev', '$commit_date', 'system', '$build_date', 'jenkins', '$build_date');"
                        echo $sql > $build_date.sql
                        "$WORKSPACE/kai-download/tools/jdk/linux/bin/java" -cp "$BASEDIR/h2-$h2_db_version.jar" org.h2.tools.RunScript -url jdbc:h2:"$BASEDIR/../database/cc/cc;PASSWORD_HASH=TRUE" -user "cc" -password "7326dd895c71012bee62e10de8d0dde52e0d20a09e518c298aa224fdf6f6547a" -script $build_date.sql
                        rm $build_date.sql                        

                        '''
                    }
                }


                stage('shell-copy-to-package-home') {
                    steps {
                        sh '''#!/bin/bash
                        PACKAGE_HOME="$WORKSPACE/kai-m-standalone-package"
                        PYLON_PATH="$WORKSPACE/pylon-framework"
                        KAI_WEB_JAR_PATH="$WORKSPACE/kai-web"
                        CC_ADM_PATH="$WORKSPACE/cc-admins"
                        CC_PATH="$WORKSPACE/cc-master"
                        CC_ADM_SHELL_PATH="$WORKSPACE/cc-shell"
                        MLCSS_PATH="$WORKSPACE/kai-m-python"

                        ####### 0000.PYLON-MANAGER to JAR PACKAGING
                        cp "$PYLON_PATH/pylon-framework-manager-webapp/build/libs/pylon-framework-manager-webapp-$kai_web_version-SNAPSHOT.jar" "$WORKSPACE/temp/pylon-framework-manager-webapp.jar"
                        unzip "$WORKSPACE/temp/pylon-framework-manager-webapp.jar" -d "$WORKSPACE/temp/pylon-framework-manager-webapp"

                        cp "$PYLON_PATH/pylon-framework-manager-webapp/build/libs/pylon-framework-manager-webapp-$kai_web_version-SNAPSHOT.jar" "$PACKAGE_HOME/frontend/kai-ml/pylon-manager/bin/pylon-framework-manager-webapp-$kai_web_version-SNAPSHOT.jar"

                        propertyFile="$WORKSPACE/temp/pylon-framework-manager-webapp/BOOT-INF/classes/revision.properties"
                        if [ -f "$propertyFile" ]
                        then
                            
                            while IFS='=' read -r key value
                            do
                                key=$(echo $key | tr '.' '_' | tr '-' '_')
                                eval ${key}=\${value}
                            done < $propertyFile
                            
                            echo "-----------------------------"
                            echo "pylon-framework version information"
                            echo "pylon-version :" $pylon_version
                            echo "revision-number :" $revision_number
                            echo "jenkins-build-number :" $BUILD_NUMBER
                            echo "-----------------------------"
                            sh "$PACKAGE_HOME/package-database/bin/cc-build-make-version.sh" "pylon-framework" "pylon-framework" $pylon_version"-"$revision_number"-"$BUILD_NUMBER "$PACKAGE_HOME/package-database/sql/cc-build-make-version.sql"
                        else
                            echo "pylon revision.properties not found"
                        fi

                        rm -rf "$WORKSPACE/temp/pylon-framework-manager-webapp.jar"
                        rm -rf "$WORKSPACE/temp/pylon-framework-manager-webapp"

                        cp "$WORKSPACE/pylon-resource/application-dev.yml.boot" "$PACKAGE_HOME/frontend/kai-ml/pylon-manager/config/application-dev.yml.sample"
                        cp "$WORKSPACE/pylon-resource/logback.xml.boot" "$PACKAGE_HOME/frontend/kai-ml/pylon-manager/config/logback.xml.sample"

                        ####### 2001.DL2JAR
                        cp "$KAI_WEB_JAR_PATH/kai-common-webapp/build/libs/kai-common-webapp-$kai_web_version-SNAPSHOT.jar" "$WORKSPACE/temp/kai-common-webapp-$kai_web_version-SNAPSHOT.jar"
                        unzip "$WORKSPACE/temp/kai-common-webapp-$kai_web_version-SNAPSHOT.jar" -d "$WORKSPACE/temp/kai-common-webapp-$kai_web_version-SNAPSHOT"

                        cp "$KAI_WEB_JAR_PATH/kai-common-webapp/build/libs/kai-common-webapp-$kai_web_version-SNAPSHOT.jar" "$PACKAGE_HOME/frontend/kai-ml/ml-web/bin/kai-common-webapp-$kai_web_version-SNAPSHOT.jar"

                        propertyFile="$WORKSPACE/temp/kai-common-webapp-$kai_web_version-SNAPSHOT/BOOT-INF/classes/revision.properties"
                        if [ -f "$propertyFile" ]
                        then
                            
                            while IFS='=' read -r key value
                            do
                                key=$(echo $key | tr '.' '_' | tr '-' '_')
                                eval ${key}=\${value}
                            done < $propertyFile
                            
                            echo "-----------------------------"
                            echo "ml-web version information"
                            echo "ml-web-version :" $kai_m_version
                            echo "revision-number :" $revision_number
                            echo "jenkins-build-number :" $BUILD_NUMBER
                            echo "-----------------------------"
                            sh "$PACKAGE_HOME/package-database/bin/cc-build-make-version.sh" "kai-m-frontend" "kai-m-frontend" $kai_m_version"-"$revision_number"-"$BUILD_NUMBER "$PACKAGE_HOME/package-database/sql/cc-build-make-version.sql"

                        else
                            echo "kai-web revision.properties not found"
                        fi

                        rm -rf "$WORKSPACE/temp/kai-common-webapp-$kai_web_version-SNAPSHOT.jar"
                        rm -rf "$WORKSPACE/temp/kai-common-webapp-$kai_web_version-SNAPSHOT"

                        cp "$WORKSPACE/kai-resource/kai-common-webapp/src/main/resources/application-dev.yml.boot" "$PACKAGE_HOME/frontend/kai-ml/ml-web/config/application-dev.yml.sample"
                        cp "$WORKSPACE/kai-resource/kai-common-webapp/src/main/resources/logback.xml.boot" "$PACKAGE_HOME/frontend/kai-ml/ml-web/config/logback.xml.sample"

                        ####### 0002.CC-ADMIN to JAR
                        cp "$CC_ADM_PATH/cc-admin-webapp/build/libs/cc-admin-webapp-1.1.0-SNAPSHOT.jar" "$PACKAGE_HOME/frontend/cc-admin/lib/cc-admin-webapp-1.1.0-SNAPSHOT.jar"
                        cp "$WORKSPACE/cc-admin-resource/application-test.yml" "$PACKAGE_HOME/frontend/cc-admin/config/application-test.yml"

                        ####### 0003.CommandCenter to JAR
                        cp "$CC_PATH/cc-boot/build/libs/cc-boot-$cc_boot_version.jar" "$PACKAGE_HOME/cc/bin/cc-boot-$cc_boot_version.jar"

                        ####### 0010.CommandCenter cc-admin-shell to JAR PACKAGING
                        cp "$CC_ADM_SHELL_PATH/cc-admin-shell/build/libs/cc-admin-shell-$cc_admin_shell_version.jar" "$PACKAGE_HOME/cc/bin/cc-admin-shell-$cc_admin_shell_version.jar"

                        ####### 0005.mlCSS_DL
                        mkdir "$PACKAGE_HOME/backend/mlCSS"
                        mkdir "$PACKAGE_HOME/backend/mlCSS/db"
                        cp -r "$MLCSS_PATH/deploy/." "$PACKAGE_HOME/backend/mlCSS/"

                        ####### default access logger
                        cp "$WORKSPACE/access-logger/koreacb-logger-$cc_boot_version.jar" "$PACKAGE_HOME/frontend/kai-ml/ml-web/libs/koreacb-logger-$cc_boot_version.jar"

                        ####### default access logger
                        cp "$WORKSPACE/access-logger/koreacb-logger-$cc_boot_version.jar" "$PACKAGE_HOME/frontend/kai-ml/pylon-manager/libs/koreacb-logger-$cc_boot_version.jar"

                        ####### DATABSE COPY
                        cp -r "$PACKAGE_HOME/package-database/database/ml-web/." "$PACKAGE_HOME/frontend/kai-ml/data"
                        cp -r "$PACKAGE_HOME/package-database/database/dbs/." "$PACKAGE_HOME/dbs"
                        cp -r "$PACKAGE_HOME/package-database/database/cc/." "$PACKAGE_HOME/cc/data"
                        cp -r "$PACKAGE_HOME/package-database/database/cc-admin/." "$PACKAGE_HOME/frontend/cc-admin/LOCAL_DB"

                        rm -rf "$PACKAGE_HOME/package-database"
                        rm -rf "$PACKAGE_HOME/frontend/ml-web"
                        rm -rf "$PACKAGE_HOME/frontend/sites"

                        rm -rf "$PACKAGE_HOME/*.iss"

                        ls -atl

                        '''
                    }
                }
                stage('shell-copy-artifact-kai-m-python-library') {
                    steps {
                        sh '''#!/bin/bash
                        cp -rf $WORKSPACE/kai-m-python-library/*.tar.gz $WORKSPACE/
                        cp -rf $WORKSPACE/kai-m-python-library/*.txt $WORKSPACE/
                        '''
                    }
                }
                stage('shell-tar-kai-m-ubuntu') {
                    steps {
                        sh '''#!/bin/bash
                        PACKAGE_HOME="$WORKSPACE/kai-m-standalone-package"

                        if [ -d "$WORKSPACE/package" ]; then
                            rm -rf "$WORKSPACE/package"
                        fi

                        mkdir "$WORKSPACE/package"

                        LINUX_PACKAGE_DIR=$WORKSPACE/package/kai-m-$(date +%Y%m%d)

                        # prepare kcbml linux
                        cd "$PACKAGE_HOME"
                        mkdir -p "$LINUX_PACKAGE_DIR/kcbml"
                        cp -r logs $LINUX_PACKAGE_DIR/kcbml
                        cp -r tools $LINUX_PACKAGE_DIR/kcbml
                        cp -r files $LINUX_PACKAGE_DIR/kcbml
                        cp -r cc $LINUX_PACKAGE_DIR/kcbml
                        cp -r dbs $LINUX_PACKAGE_DIR/kcbml
                        cp -r frontend $LINUX_PACKAGE_DIR/kcbml
                        cp -r backend $LINUX_PACKAGE_DIR/kcbml
                        cp -r maintenance $LINUX_PACKAGE_DIR/kcbml

                        chmod +x common.conf
                        chmod +x $PACKAGE_HOME/docker_build/update-cc-admin-sql.sh
                        cp *.sh $LINUX_PACKAGE_DIR/kcbml
                        cp common.conf $LINUX_PACKAGE_DIR/kcbml
                        cp "$PACKAGE_HOME/docker_build/h2-$h2_db_version.jar" $LINUX_PACKAGE_DIR/kcbml/
                        cp $PACKAGE_HOME/docker_build/update-cc-admin-sql.sh $LINUX_PACKAGE_DIR/kcbml/
                        echo "key=$licence_key" > $LINUX_PACKAGE_DIR/kcbml/cc/lic.properties

                        # prepare kaisc linux
                        cd "$PACKAGE_HOME"

                        mkdir -p "$LINUX_PACKAGE_DIR/kaisc"
                        tar xzf $WORKSPACE/temp/$(ls $WORKSPACE/temp | sort -r | head -1)  -C $LINUX_PACKAGE_DIR/kaisc
                        # mv $LINUX_PACKAGE_DIR/kaisc-package $LINUX_PACKAGE_DIR/kaisc

                        # prepare tools
                        mkdir -p "$LINUX_PACKAGE_DIR/tools"

                        cp -r $WORKSPACE/kai-download/Miniconda3-py38_4.10.3-Linux-x86_64.sh $LINUX_PACKAGE_DIR/tools
                        cp -r $WORKSPACE/kai-download/openjdk8-ubuntu-16.04.tar.gz $LINUX_PACKAGE_DIR/tools
                        cp -r $WORKSPACE/kai-download/python_pkgs.tar.gz $LINUX_PACKAGE_DIR/tools
                        cp -r $WORKSPACE/kai-download/ubuntu-deb.tar.gz $LINUX_PACKAGE_DIR/tools

                        cp $PACKAGE_HOME/tools/install.sh $LINUX_PACKAGE_DIR/
                        cp $WORKSPACE/python_pkgs.tar.gz $LINUX_PACKAGE_DIR/tools
                        cp $WORKSPACE/kai_m_requirement.txt $LINUX_PACKAGE_DIR/tools

                        # JDK 압축을 풀어야 함. (Install.sh에서 실행 함)
                        # cd "$LINUX_PACKAGE_DIR/tools"
                        # tar -xzvf openjdk8-ubuntu-16.04.tar.gz

                        tar czf $WORKSPACE/package/kai-m-ubuntu-16.04-$(date +%Y%m%d).tar.gz -C $(dirname $LINUX_PACKAGE_DIR) $(basename $LINUX_PACKAGE_DIR)
                        '''
                    }
                }
                stage('shell-create-ML_RUN_PROCESS.pyc') {
                    steps {
                        sh '''#!/bin/bash
                        
                        ######################################################
                        # 1. install.sh 위치에 수정 사항
                        ######################################################

                        LINUX_PACKAGE_DIR=$WORKSPACE/package/kai-m-$(date +%Y%m%d)
                   
                        ######################################################
                        # 2. install.sh 수정사항
                        ######################################################
                        if [ ! -f $LINUX_PACKAGE_DIR/kcbml/backend/mlCSS/ML_RUN_PROCESS.pyc ]; then
                            # 배포된 서버의 해당 폴더에 파이썬을 실행 시켜 줌. 
                            cp $WORKSPACE/kai-m-python/ML_RUN_PROCESS.py $LINUX_PACKAGE_DIR/kcbml/backend/mlCSS/
                            $LINUX_PACKAGE_DIR/tools/python/bin/python3.8 -m py_compile $LINUX_PACKAGE_DIR/kcbml/backend/mlCSS/ML_RUN_PROCESS.py
                            mv $LINUX_PACKAGE_DIR/kcbml/backend/mlCSS/__pycache__/ML_RUN_PROCESS.*.pyc $LINUX_PACKAGE_DIR/kcbml/backend/mlCSS/ML_RUN_PROCESS.pyc
                        fi

                        # 캐시 파일이 생성 되었으면, 삭제 해도 됨
                        if [ -f $LINUX_PACKAGE_DIR/kcbml/backend/mlCSS/ML_RUN_PROCESS.py ]; then
                            rm $LINUX_PACKAGE_DIR/kcbml/backend/mlCSS/ML_RUN_PROCESS.py
                        fi

                        ######################################################
                        # 3. startup.sh 파일명 수정 / install.sh 파일에 추가해도 됨.
                        ######################################################
                        mv $LINUX_PACKAGE_DIR/kcbml/startup.sh $LINUX_PACKAGE_DIR/kcbml/start.sh
                        mv $LINUX_PACKAGE_DIR/kaisc/startup.sh $LINUX_PACKAGE_DIR/kaisc/start.sh

                        '''
                    }
                }
                stage('shell-install-and-copy-and-run') {
                    steps {
                        sh '''#!/bin/bash
                        LINUX_PACKAGE_DIR=$WORKSPACE/package/kai-m-$(date +%Y%m%d)
                        cd $LINUX_PACKAGE_DIR
                        pkill -9 -ef kai
                        ./install.sh new
                        ######################################################
                        # 2. install.sh 수정사항
                        ######################################################
                        KCBML_HOME=$LINUX_PACKAGE_DIR/new/kcbml
                        TOOLS=$LINUX_PACKAGE_DIR/new/tools

                        cp $WORKSPACE/kai-m-python/ML_RUN_PROCESS.py $KCBML_HOME/backend/mlCSS/

                        if [ ! -f $KCBML_HOME/backend/mlCSS/ML_RUN_PROCESS.pyc ]; then
                            # 배포된 서버의 해당 폴더에 파이썬을 실행 시켜 줌.
                            $TOOLS/python/bin/python3.8 -m py_compile $KCBML_HOME/backend/mlCSS/ML_RUN_PROCESS.py
                            mv $KCBML_HOME/backend/mlCSS/__pycache__/ML_RUN_PROCESS.*.pyc $KCBML_HOME/backend/mlCSS/ML_RUN_PROCESS.pyc
                        fi

                        # 캐시 파일이 생성 되었으면, 삭제 해도 됨
                        if [ -f $KCBML_HOME/backend/mlCSS/ML_RUN_PROCESS.py ]; then
                            rm $KCBML_HOME/backend/mlCSS/ML_RUN_PROCESS.py
                        fi

                        # cd $KCBML_HOME

                        # nohup ./start.sh > /dev/null 2>&1 &

                        '''
                    }
                }
            }
        }
	}
}