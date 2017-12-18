# Develog
[![Build status](https://travis-ci.org/T3r1jj/Develog.svg?branch=master)](https://travis-ci.org/T3r1jj/Develog) [![codecov](https://codecov.io/gh/T3r1jj/Develog/branch/master/graph/badge.svg)](https://codecov.io/gh/T3r1jj/Develog) [![Lines of code](https://tokei.rs/b1/github/T3r1jj/Develog)](https://github.com/Aaronepower/tokei).


Develog is a web application inspired by James Routley's idea of [using a logbook to improve your programming](https://routley.io/tech/2017/11/23/logbook.html). The app allows users to create one private and tagged note per day. Simple Markdown together with PlantUML speeds up and facilitates the process of describing a solution to a problem.
 
---

### Development
The app is currently under development in educational purposes, with a few requirements.

![Code coverage by commits](https://codecov.io/gh/T3r1jj/Develog/branch/master/graphs/commits.svg)
 
Basic requirements:
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
    2. ~~image insertion by pasting~~ (db size)
    3. Markdown support
    4. PlantUML (diagrams) support
    5. two panels side by side - editor and view
    6. export to txt/html
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
- Spring + Spring Boot + Spring Actuators
- Thymeleaf
- Materialize + _LESS/SASS_
- Wro4j
- PostgreSQL
- AspectJ
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
- Codecov

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