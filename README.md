## What is Bluetooth?
Bluetooth is a short-range wireless technology standard. It utilizes UHF radio waves in the ISM bands from 2.402GHz to 2.48GHz. It is a useful technology for data transfers. It's now found in various devices and is popular for everything from music streaming to file sharing.

## What is Bluetooth Low Energy?
Bluetooth Low Energy is based on Bluetooth. It was released in 2011, and it is also referred to as Bluetooth Smart and Bluetooth 4.0.BLE is designed to offer many of the same features as Bluetooth but focusing on low power. As a result, it is not as fast as Bluetooth and is not suitable for transferring large files. But it is ideal for transferring small amounts of data with minimal power consumption. Android provides built-in platform support for Bluetooth Low Energy (BLE) in the central role and provides APIs that apps can use to discover devices, query for services, and transmit information. BLE APIs help you to communicate with BLE devices smoothly with less battery consumption.

### Fundamentals of BLE
For transmitting data between BLE-enabled devices you need to follow these steps:

Mention the required permissions in Manifest.xml.
they must first form a channel of communication.
Then access BluetoothAdapter and scan for available BLE devices nearby.
Once a device is found, the capabilities of the BLE device are discovered by connecting to the GATT server on the BLE device.
Once the connection is established transmit data.

### How Secure Is Bluetooth Low Energy?
All BLE connections are equipped with AES-128 end-to-end encryption. This prevents data from being read if it's ever intercepted.

## Keywords
*** Profile is a specification of how a device works in a particular application. A device can implement more than one profile.
**** The GATT profile is a general specification for sending and receiving short pieces of data known as “attributes” over a BLE link. Using this profile we can transmit data between BLE devices.
**** A characteristic contains a single value and optional descriptors that describe the characteristic’s value.
**** Descriptors are defined attributes that describe a characteristic value. They can be used to describe the characteristic’s features or to control certain behaviors of the characteristic.
**** Service contains a collection of characteristics.
**** Advertisement means when a BLE peripheral device broadcasts packets to every device around it. The receiving device can then act on this information or connect to receive more information.
