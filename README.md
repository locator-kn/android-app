# android-app
native android application


## enable wlan-deploying / -debugging
1. ensure your pc and your mobile phone is connected to wlan
2. open android studio / project
3. connect your mobile phone to your computer
4. type into android studio terminal:
    `adb tcpip 5555`
5. disconnect your mobile phone from your computer
6. type into android studio terminal:
    `abd connect x.x.x.x`,
    where x.x.x.x is the IP address of your mobile phone
7. click 'run' --> the app will deployed to your mobile phone via wlan