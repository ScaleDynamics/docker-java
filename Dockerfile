FROM openjdk:17
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN javac Main.java
EXPOSE 8080
CMD ["java", "Main"]