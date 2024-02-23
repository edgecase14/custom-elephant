# custom-elephant

## dev environment:
- Ubuntu 22.04
- Eclipse.org 2022-03 Eclipse IDE for Enterprise Java and Web Developers
  Jboss community plugin - Help - Install Software... 2022-03 download.eclipse.org
- Wildfly.org 26.0.1.Final - EE 8 edition
- OpenJDK 11 - ubuntu pkg openjdk-11-jdk-headless
- git - ubuntu pkg
  Postgresql - ubuntu pkg

## steps:
- unzip Wildfly, add to Eclipse workspace config
- git checkout
- import projects
- set jre to OpenJDK 11 for workspace, wildfly
- Project Explorer - right-click elephant-ear - Run As - Server
- Server perspective, elephant-ear - Full publish
- test in browser http://localhost:8080/elephWeb

