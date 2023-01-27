ARG V_TOMCAT=10.1-jdk17
FROM tomcat:${V_TOMCAT}

RUN rm -rf /usr/local/tomcat/webapps/ROOT/
ADD conference_web/build/libs/conference_web.war /usr/local/tomcat/webapps/ROOT.war

LABEL maintainer="zsolt.lukacs@microfocus.com"
LABEL VERSION="1.0-SNAPSHOT"

EXPOSE 8080
CMD ["catalina.sh", "run"]