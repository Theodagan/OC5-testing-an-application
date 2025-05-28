{ pkgs, ... }:
{
  channel = "stable-23.11";

  packages = [
    pkgs.openjdk11
    pkgs.nodejs_20
    pkgs.mysql80
    pkgs.maven
    pkgs.git
    pkgs.docker-compose
    pkgs.nodePackages."@angular/cli"
    pkgs.gh
  ];

  env = {
    MYSQL_USER = "user";
    MYSQL_PASSWORD = "123456";
    MYSQL_DATABASE = "test";
    MYSQL_PORT = "3306";
    FRONTEND_PORT = "4200";
  };

  services.mysql.enable = true;
  services.docker.enable = true;

  idx = {
    extensions = [
      "angular.ng-template"
      "vscjava.vscode-java-pack"
      "redhat.java"
    ];

    workspace = {
      onCreate = {
        install = "cd back/ && mvn clean install -DskipTests && cd ../front/ && npm install";      
      };
      onStart = {
        runServer = "(cd back/ && mvn spring-boot:run) & cd front/ && ng serve  ";      
      };
    };

  };
}