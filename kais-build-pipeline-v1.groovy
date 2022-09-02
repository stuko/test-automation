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
        string(name: 'kai_s_runtime_build_target', defaultValue: 'master', description: '')
        string(name: 'rule_manager_build_target', defaultValue: 'master', description: '')
		string(name: 'cc_projects', defaultValue: 'cc-utils,cc-boot,cc-admin-command,cc-extend-command,modelindex2,cc-quartz-scheduler1.1,cc-extend-utils', description: '')
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
        
    }
    tools {
        gradle "Gradle 4.4"
    }

	
	stages {
	
        stage('initialize') {
            steps {
		        sh 'sleep 1'
				// sh 'rm -rf $WORKSPACE/*'
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

		stage('checkout-modules') {
			stages {
				stage('git-pull-kai-ms-deployment') {steps {checkout([$class: 'GitSCM', 
															branches: [[name: "${params.kai_s_runtime_build_target}"]], 
															doGenerateSubmoduleConfigurations: false, 
															extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'kai-s-runtime']], 
															submoduleCfg: [], 
															userRemoteConfigs: [[credentialsId: 'admin', 
																				url: 'http://192.168.57.237:19000/KAI-S/kai-s-runtime']]
															])}}

				stage('git-pull-kai-download') {steps {checkout([$class: 'GitSCM', 
														branches: [[name: "jdk"]], 
														doGenerateSubmoduleConfigurations: false, 
														extensions: [[$class: 'RelativeTargetDirectory',relativeTargetDir: 'kai-download'],
																		[$class: 'CloneOption', timeout: 6000000]], 
														submoduleCfg: [], 
														userRemoteConfigs: [[credentialsId: 'admin', 
																			url: 'http://admin@192.168.57.237:19000/admin/kai-download']]
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
																		local: './pylon-framework', 
																		remote: 'http://192.168.57.237:19000/svn/COMMON/pylon-framework/pylon-framework']], 
														quietOperation: true, 
														workspaceUpdater: [$class: 'UpdateUpdater']])}}
				stage('checkout-kais-package-dir') {
					steps {
						checkout([$class: 'SubversionSCM',
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
									local: './kai-s-web-package', 
									remote: 'http://192.168.57.237:19000/svn/KAI-S/deployment/kai-s-web-package']], 
						quietOperation: true, 
						workspaceUpdater: [$class: 'UpdateUpdater']])
					}
				}		

				stage('kais-web-resource-copy') {
					steps {
						checkout([$class: 'GitSCM', 
								branches: [[name: "${params.rule_manager_build_target}"]], 
								doGenerateSubmoduleConfigurations: false, 
								extensions: [[$class: 'RelativeTargetDirectory', 
											relativeTargetDir: 'kais-resource']], 
								submoduleCfg: [], 
								userRemoteConfigs: [[credentialsId: 'admin', 
													url: 'http://admin@192.168.57.237:19000/KAI-S/kai-s-web']]])

						dir('kais-resource/kais-common-webapp/src/main/resources') {
							sh 'cp "application-kais-pkg.yml" "$WORKSPACE/kai-s-web-package/frontend/kais-web/config/application-kais-pkg.yml"'
						}
					}
				}

				stage('pylon-resource-copy') {
					steps {
						checkout([$class: 'SubversionSCM',
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
											remote: 'http://192.168.57.237:19000/svn/COMMON/pylon-framework/pylon-framework/pylon-framework-manager-webapp/src/main/resources']], 
								quietOperation: true, 
								workspaceUpdater: [$class: 'UpdateUpdater']])

						dir('pylon-resource') {
							sh 'cp "application-kais-pkg.yml" "$WORKSPACE/kai-s-web-package/frontend/pylon-manager/config/application-kais-pkg.yml"'
						}
					}
				}				

			}
		}

		stage('shell-jdk'){
			steps{
				sh '''#!/bin/bash
				mkdir -p kai-download/tools/jdk/linux
				tar -xzf $WORKSPACE/kai-download/linux_jdk.tar.gz -C $WORKSPACE/kai-download/tools/jdk/linux/
				ln -sfn $WORKSPACE/kai-download/tools/jdk/linux ~/java_home
				JAVA_HOME="~/java_home"
				# $WORKSPACE/kai-download/tools/jdk/linux/bin/java <-- Exe file
				'''
			}
		}


		stage('build-kai-s-runtime') {
			steps {
				sh '''#!/bin/bash
				cd $WORKSPACE/kai-s-runtime
				chmod a+x $WORKSPACE/kais-resource/gradlew
				$WORKSPACE/kais-resource/gradlew clean
				$WORKSPACE/kais-resource/gradlew build --exclude-task test --refresh-dependencies uploadArchives
				'''
			}
		}

		
		stage('integrating-depedencies-parallel') {
			stages {
				stage('kais-web-jar-process') {
					steps {
						build job: '3003.Kais-Web-JAR', parameters: [gitParameter(name: 'buildTarget', value: "${params.rule_manager_build_target}")]
						copyArtifacts filter: '**/kais-common-webapp-*.jar', fingerprintArtifacts: true, flatten: true, projectName: '3003.Kais-Web-JAR', target: 'kai-s-web-package/frontend/kais-web/bin/'
					}
				}
				
				stage('kais-web-db-process') {
					steps {
						build job: '3003.Kai-s-web-DATABASE', parameters: [gitParameter(name: 'buildTarget', value: "${params.rule_manager_build_target}")]
						copyArtifacts filter: 'resource_db/**/*.zip', fingerprintArtifacts: true, flatten: true, projectName: '3003.Kai-s-web-DATABASE', target: 'kai-s-web-package'
						unzip dir: 'kai-s-web-package/data', glob: '', zipFile: 'kai-s-web-package/database.zip'
						unzip dir: 'kai-s-web-package/tools/init/database', glob: '', zipFile: 'kai-s-web-package/database.zip'
						unzip dir: 'kai-s-web-package/package-database/database', glob: '', zipFile: 'kai-s-web-package/database.zip'
                        sh 'sleep 1'
						sh 'rm -rf kai-s-web-package/database.zip'
					}
				}
				
				stage('build-pylon-framework') {
											steps {
												sh '''#!/bin/bash
												cd $WORKSPACE/pylon-framework
												chmod a+x $WORKSPACE/kais-resource/gradlew
												$WORKSPACE/kais-resource/gradlew clean
												$WORKSPACE/kais-resource/gradlew build --refresh-dependencies
												$WORKSPACE/kais-resource/gradlew jar
												'''
											}
											post{
												success{
													archiveArtifacts artifacts: 'pylon-framework/pylon-framework-manager-webapp/build/libs/pylon-framework-manager-webapp-*.jar'
												}
											}
										}
				
				stage('pylon-manager-process') {
					steps {
                        sh '''#!/bin/bash
						cp $WORKSPACE/pylon-framework/pylon-framework-manager-webapp/build/libs/pylon-framework-manager-webapp-*.jar $WORKSPACE/kai-s-web-package/frontend/pylon-manager/bin/
						'''
					}
                }
				
				stage('command-center-to-jar') {
					steps {
						build job: '0003.CommandCenter to JAR', parameters: [string(name: 'projects', value: "${params.cc_projects}")]
						copyArtifacts filter: '**/cc-boot-*.jar', fingerprintArtifacts: true, flatten: true, projectName: '0003.CommandCenter to JAR', target: 'kai-s-web-package/cc/libs'
					}
				}
				
				stage('kais-core-to-jar') {
					steps {
						build job: '3003.Kais-core-JAR', parameters: [gitParameter(name: 'buildTarget', value: "${params.rule_manager_build_target}")]
						copyArtifacts filter: '**/kais-core-*.jar', fingerprintArtifacts: true, flatten: true, projectName: '3003.Kais-core-JAR', target: 'kai-s-web-package/cc/libs'
					}
				}

				stage('kais-cc-service-to-jar') {
					steps {
						build job: '3002.kais-cc-service to JAR-git'
						copyArtifacts filter: '**/kais-cc-service-*.jar', fingerprintArtifacts: true, flatten: true, projectName: '3002.kais-cc-service to JAR-git', target: 'kai-s-web-package/cc/libs'
					}
				}
				
			
		
		
			}
		}
		
		stage('packaging') {
			steps {
				dir ('kai-s-web-package') {
					script {
						try {
                                sh 'sleep 1'
								sh '''#!/bin/bash
									find . -name .svn -exec rm -rf '{}' \\;
									'''
							}
						catch (e) {
						
						}
						
						try {
                                sh 'sleep 1'
								sh '''#!/bin/bash
								echo "install windows gcc library"
								unzip "/data/jenkins/kai-kcbml-standalone/gcc_lib.zip" -d "tools/gcc_lib"
								
								case "$jdk" in
									*windows-jdk*)
										echo "install windows zulu jdk 1.8 version"
										
										unzip "/app/data/jenkins/kai-kcbml-standalone/zulu_jdk.zip" -d "tools"
										
										echo "tomcat native dll copy to windows jdk/bin"
										cp "tools/tcnative/tcnative-1.dll" "tools/jdk/windows/bin"
										
									;;
								esac
									
								case "$jdk" in
									*linux-jdk*)
										echo "install linux openjdk jdk 1.8 version"
										
										mkdir -p "tools/jdk/linux"
										cp "/data/jenkins/kai-kcbml-standalone/linux-jdk.tar.gz" "tools/jdk/linux"
									;;    
								esac
									'''
							}
						catch (e) {
						
						}
						
						if ("$jdk" == 'windows-jdk') {
                            sh 'sleep 1'
							sh 'zip -r "kais-rulemanager-$(date +%Y%m%d).zip" *'
						} else {
                            sh 'sleep 1'
							sh 'tar -zcvf "kais-rulemanager-$(date +%Y%m%d).tgz" *'
						}
						sh 'sleep 1'
						sh 'ln -s /usr/bin tools/jdk/linux'

					}

					
				}
			}
		}
		
		stage('deploy') {
			steps {
				script {
                    sh 'sleep 1'
					sh 'chmod -R 777 kai-s-web-package'
					if ("$jdk" == 'windows-jdk') {
						archiveArtifacts artifacts: 'kai-s-web-package/kais-rulemanager-*.zip', followSymlinks: false				
					} else {
						archiveArtifacts artifacts: 'kai-s-web-package/kais-rulemanager-*.tgz', followSymlinks: false				
					}
                    sh 'sleep 1'
					sh 'cp -rf ~/db-server/data/web/KAIS-WEB.mv.db $WORKSPACE/kai-s-web-package/data/web/'
                    sh 'sleep 1'
                    sh 'cd $WORKSPACE/kai-s-web-package/ && ./shutdown.sh'
					sh 'sleep 5'
					sh 'cd $WORKSPACE/kai-s-web-package/ && ./startup.sh'
				}
			}
		}
		
		
	}

	
}