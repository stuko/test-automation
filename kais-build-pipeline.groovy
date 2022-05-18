pipeline {
	agent {label "test-automation"}
	tools {
		gradle "Gradle 4.4"
	}

	
	stages {
	
        stage('initialize') {
            steps {
		        sh 'sleep 1'
				sh 'rm -rf $WORKSPACE/*'
			}
        }

		stage('upload-archive') {
		    steps {
                build job: 'test-automation-4000.KAI-S-UploadArchives-git', parameters: [gitParameter(name: 'kai_s_runtime_build_target', value: "${params.kai_s_runtime_build_target}")]
			}
		}
		
		stage('checkout-subDir-parallel') {
			parallel {
				stage('checkout-kais-package-dir') {
					steps {
						checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[cancelProcessOnExternalsFail: true, credentialsId: 'admin', depthOption: 'infinity', ignoreExternalsOption: true, local: './kai-s-web-package', remote: 'http://192.168.57.237:19000/svn/KAI-S/deployment/kai-s-web-package']], quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
					}
				}
				
				stage('kais-web-resource-copy') {
					steps {
						checkout([$class: 'GitSCM', branches: [[name: "${params.ruleManagerBuildTarget}"]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'kais-resource']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'admin', url: 'http://admin@192.168.57.237:19000/KAI-S/kai-s-web']]])
						dir('kais-resource/kais-common-webapp/src/main/resources') {
							sh 'cp "application-kais-pkg.yml" "$WORKSPACE/kai-s-web-package/frontend/kais-web/config/application-kais-pkg.yml"'
						}
					}
				}
				
				stage('pylon-resource-copy') {
					steps {
						checkout([$class: 'SubversionSCM', additionalCredentials: [], excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '', excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', locations: [[cancelProcessOnExternalsFail: true, credentialsId: 'admin', depthOption: 'infinity', ignoreExternalsOption: true, local: './pylon-resource', remote: 'http://192.168.57.237:19000/svn/COMMON/pylon-framework/pylon-framework/pylon-framework-manager-webapp/src/main/resources']], quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
						dir('pylon-resource') {
                            sh 'sleep 1'
							sh 'cp "application-kais-pkg.yml" "$WORKSPACE/kai-s-web-package/frontend/pylon-manager/config/application-kais-pkg.yml"'
						}
					}
				}
			}
		}
		

		stage('integrating-depedencies-parallel') {
			parallel {
				stage('kais-web-jar-process') {
					steps {
						build job: '3003.Kais-Web-JAR', parameters: [gitParameter(name: 'buildTarget', value: "${params.ruleManagerBuildTarget}")]
						copyArtifacts filter: '**/kais-common-webapp-*.jar', fingerprintArtifacts: true, flatten: true, projectName: '3003.Kais-Web-JAR', target: 'kai-s-web-package/frontend/kais-web/bin/'
					}
					
					
				}
				
				stage('kais-web-db-process') {
					steps {
						build job: '3003.Kai-s-web-DATABASE', parameters: [gitParameter(name: 'buildTarget', value: "${params.ruleManagerBuildTarget}")]
						copyArtifacts filter: 'resource_db/**/*.zip', fingerprintArtifacts: true, flatten: true, projectName: '3003.Kai-s-web-DATABASE', target: 'kai-s-web-package'
						unzip dir: 'kai-s-web-package/data', glob: '', zipFile: 'kai-s-web-package/database.zip'
						unzip dir: 'kai-s-web-package/tools/init/database', glob: '', zipFile: 'kai-s-web-package/database.zip'
						unzip dir: 'kai-s-web-package/package-database/database', glob: '', zipFile: 'kai-s-web-package/database.zip'
                        sh 'sleep 1'
						sh 'rm -rf kai-s-web-package/database.zip'
					}
				}
				
				
				stage('pylon-manager-process') {
					steps {
						build job: 'test-automation-0000.PYLON-MANAGER to JAR'
						copyArtifacts filter: '**/pylon-framework-manager-webapp-*.jar', fingerprintArtifacts: true, flatten: true, projectName: 'test-automation-0000.PYLON-MANAGER to JAR', target: 'kai-s-web-package/frontend/pylon-manager/bin'
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
						build job: '3003.Kais-core-JAR', parameters: [gitParameter(name: 'buildTarget', value: "${params.ruleManagerBuildTarget}")]
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