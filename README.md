
# DIY EV APP  
This is an android app that I made for my Hyundai Kona. This app can read a ODB2 dongle that is plugged into the car and read data like SoC, SoH. This app does **not** have a graphical interface. The graphical interface should be handled by a seperate front-end or you should build is yourself. 

### How does it work?
#### Receiving the ODB2 data
Whenever you open the app a new thread is started with the class BluetoothConnectionThread. This thread will connect to you ODB2 dongle via bluetooth. In the BluetoothConnectionThread class you have to define the MAC adress of your ODB2 dongle and inside the MyBluetooth class you have to define the uuid of the bluetooth service. You can find the mac adress and uuid with bluetoothscanner apps in the playstore.

After the connection is established you can press the start button. The start button will start two tasks. One task is the GPSTracker, this task will periodically retrieve the GPS information of the mobile phone. The second task is the CarInformationBroadcastReceiver, this task will periodically read the ODB2 dongle. 

Eventually (since bluetooth works with a lot of callbacks....) the ODB2MessageProcessor will receive all messages send by the ODB2 Dongle. Note that the data from the ODB2 dongle is binary data! When all data is received we will post all binary to a server. 
#### Parsing the binary data
In my setup I parse the binary data I receive from the ODB2 dongle on a webserver. This is done with the classes in the package "org.open.ev.app.odb2parsers".  The starting point for this is the ODB2Parser class. 
### Final note
I know the code is not the prettiest code in the world. This was a hobby project of mine and my mission was to make it work, not make it pretty. All this code is based on the [EVNotify project](https://github.com/EVNotify/EVNotify/blob/master/app/www/components/cars/KONA_EV.vue) . So for reference you can also look at this project. 

In theory you can parse any data that the Torque app can parse. If you want to see all currently available Torque app data check out [TorqueCSVSheet](https://github.com/JejuSoul/OBD-PIDs-for-HKMC-EVs/tree/master/Hyundai%20Kona%20EV%20%26%20Kia%20Niro%20EV) .
