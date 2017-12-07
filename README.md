# Develog

Develog is a web application inspired by James Routley's idea of [using a logbook to improve your programming](https://routley.io/tech/2017/11/23/logbook.html). The app allows users to create one private and tagged note per day. Simple Markdown together with PlantUML speeds up and facilitates the process of describing a solution to a problem. 

### Requirements
The app is currently being developed in educational purposes with a few requirements. Basic requirements:
1. Project management tool (i.e. Maven/Gradle)
2. Dependency injection framework (i.e. Spring with Inversion of Control)
3. Object Relational Mapping (i.e. JPA) - min. 3 relational tables
4. Aspects
5. Docker
6. Integration and unit tests
7. DAO/Repository based data layer, MVC based presentation layer
7. User authentication

### Features 
a) USER:
1. note creation and edition (single note, for present day)
    1. tags
    2. _image insertion by pasting_
    3. Markdown support
    4. PlantUML (diagrams) support
    5. two panels side by side - editor and view
    6. pdf generating
2. _one global note (see 1.)_
3. notes listing by days
4. notes listing by tags (append into one?)
5. searching by date/tags
6. archiving (i.e. download as zip)

b) ADMIN:
1. same as a)
2. monitoring - requests/performance/user data size
3. banning
4. notifying users by email

c) BANNED:
1. same as a) but cannot insert/edit

### Technologies/Components/Services

- Maven
- Spring + Spring Boot
- Thymeleaf
- Materialize + _LESS/SASS_
- Wro4j
- PostgreSQL
- AspectJ
- Elasticsearch + Kibana (Monitoring)
- Lombok
- Flyingsaucer (PDF)
- PlantUML
- Flexmark-java (Markdown)
- Selenium
- JUnit5
- Mockito
- Code Analysis tool
- Code Coverage tool (OpenClover)
- Travis CI
- Coveralls

### License

   Copyright 2017 Damian Terlecki

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.