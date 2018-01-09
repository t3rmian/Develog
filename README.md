# Develog
[![Build status](https://travis-ci.org/T3r1jj/Develog.svg?branch=master)](https://travis-ci.org/T3r1jj/Develog) [![codecov](https://codecov.io/gh/T3r1jj/Develog/branch/master/graph/badge.svg)](https://codecov.io/gh/T3r1jj/Develog) [![Lines of code](https://tokei.rs/b1/github/T3r1jj/Develog)](https://github.com/Aaronepower/tokei).


Develog is a web application inspired by James Routley's idea of [using a logbook to improve your programming](https://routley.io/tech/2017/11/23/logbook.html). The app allows users to create one private and tagged note per day. Simple Markdown together with PlantUML speeds up and facilitates the process of describing a solution to a problem.


---

### Features 
a) USER:
1. note creation and edition (single note, for present day)
    1. tags
    2. Markdown support
    3. PlantUML (diagrams) support
    4. two panels side by side - editor and view
    5. export to txt/html
2. one global note (see 1.)
3. calendar
4. searching by date/tags/everything(*)
5. archiving:
    1. raw
    2. html

b) ADMIN:
1. same as a)
2. monitoring:
    1. user data size
    2. user online stats
    3. error logging
    4. business methods performance
    5. general status and app health
3. changing roles
4. notifying users by email

c) BANNED:
1. same as a) but cannot insert/edit

Other:
1. i18n
2. RWD
3. GitHub based authentication, authorization


### Technologies/Components/Services

- Maven
- Spring + Spring Boot + Spring Actuators
- Thymeleaf
- jQuery
- Materialize
- Wro4j
- PostgreSQL
- MongoDB
- AspectJ
- Lombok
- PlantUML
- Markdown (Flexmark-java)
- JUnit5
- Mockito
- Code Analysis tool
- Code Coverage tool (OpenClover)
- Travis CI
- Codecov
- Docker

### Config

PlantUML requires a GraphViz installation for full drawing support. This is why we use a docker image. All env variables required to start the app can be found in **docker/env_sample** (GitHub Oauth2, PostgreSQL, MongoDB).

Run test coverage: __mvn clean clover:setup verify clover:aggregate clover:clover__  
Dockerize: __mvn clean package spring-boot:repackage docker:build__  
Run in docker: __docker run --env-file=docker/env_sample -p 8080:8080 -t develog__  

After setting PostgreSQL (postgres) and MongoDB (mongo) containers you can simply run docker/start.sh 

When hosting on Heroku without docker, an additional [GraphViz](https://elements.heroku.com/buildpacks/dsander/heroku-buildpack-graphviz#buildpack-instructions) build pack is required.

---

### License

    Develog - a web logbook for developers
    Copyright (C) 2017 Damian Terlecki

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.