# Study-App
## 1.0 Introduction
This project is an m-learning Android application which uses Wi-Fi Direct as its mean of communication. It is my final year project for my undergraduate studies. It allows various people to share knowledge with another through the distribution of quizzes and study materials. Different from the existing m-learning Android applications in the market, the Study App allows the users to use the application without having the need of the Internet, which provide more portability for the users share knowledge, as they are not restricted by the Wi-Fi hotspot coverage limitation.

Furthermore, in this project I demostrated the usage of the Wi-Fi Direct technology supported by the Android framework starting from API level 14 onwards. Throughout the development of the application, various issues regarding the Android Wi-Fi Direct APIs were found. Some of them were resolved through reflections and some of them through some other workarounds. In the end, the Android Wi-Fi Direct APIs still have some flaws and it will be good if Google can improve their Wi-Direct APIs in the future.

## 2.0 Methodology
### 2.1 Architecture
#### 2.1.1 Block diagram
![Block diagram](https://github.com/ginsan95/Study-App/blob/master/demo/doc/Block%20Diagram.png?raw=true)

#### 2.1.2 Connection architecture digram
In this diagram it shows the client server view of the application.

![Connection architecture digram](https://github.com/ginsan95/Study-App/blob/master/demo/doc/Connection%20Architecture%20Diagram.png?raw=true)

#### 2.1.3 Teacher architecture digram
In this diagram it shows the MVC view of the teacher side.

![Teacher architecture digram](https://github.com/ginsan95/Study-App/blob/master/demo/doc/Teacher%20Architecture%20Diagram.png?raw=true)

#### 2.1.4 Student architecture digram
In this diagram it shows the MVC view of the student side.

![Student architecture digram](https://github.com/ginsan95/Study-App/blob/master/demo/doc/Student%20Architecture%20Diagram.png?raw=true)

### 2.2 Database
For the database, I am using SQLite to store the quizzes and study materials data, where I will be using the SQLite APIs from the Android framework to perform my neccessary sql queries. Below shows the EERD of the database.

![EERD](https://github.com/ginsan95/Study-App/blob/master/demo/doc/EERD.png?raw=true)

## 3.0 Screenshots
### 3.1 Class navigation system
![Class navigation system](https://github.com/ginsan95/Study-App/blob/master/demo/screenshots/class%20navigation%20system.png?raw=true)

### 3.2 Student class system
![Class navigation system](https://github.com/ginsan95/Study-App/blob/master/demo/screenshots/teacher%20class%20system.png?raw=true)

### 3.3 Teacher class system
![Class navigation system](https://github.com/ginsan95/Study-App/blob/master/demo/screenshots/student%20class%20system.png?raw=true)
