
'''
Created on 30/mag/2015

@author: damiannew
'''
import requests, json, time
import RPi.GPIO as GPIO

class MyUser():

    def __init__(self):
        self.status   = -1
        self.place_id = -1
        
    def process_rqs(self, pin_in, status, place_id, station_id, security_checker):
        alarm = 0
        wait  = 5          #wait time in second, in this time the user has to pass the smartphone on NFC, otherwise
                            #he is registered as an unregistered user
        
        if status==1: #falling edge
	    print str(status)+'   ho messo la bici'
            #time.sleep(wait)
            if GPIO.input(pin_in) == False:
                self.lock_rqst(status, place_id, station_id)
                self.update_dbServer(status,place_id, station_id)
            else:
                pass
        else:
            print str(status)+ '   ho tolto la bici'
            response = self.steal_cntrl(status, place_id, station_id)
            if int(response.json().get("station_id")) == -1 or int(response.json().get("place_id")) == -1:
                alarm = 1
		print response.json().get("station_id")
            elif (security_checker == 1): 
                alarm = 0
                print 'station_id' +str(response.json().get("station_id"))
                self.lock_rqst(status, place_id, station_id)
                self.update_dbServer(status, place_id, station_id)
	    else:
                self.update_dbServer(status, place_id, station_id)
        print "alarm "+str(alarm)        
        return alarm
    
    def lock_rqst(self, status, place_id, station_id):
        url      = "http://10.42.0.1:7000/myBP_server/users/lock_ras"
        payload = { 'station_id': str(station_id), 'place_id': str(place_id), 'status': str(status) }
        headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
        requests.post(url, data=json.dumps(payload), headers = headers)
        
    def steal_cntrl(self, status, place_id, station_id):
        url      = "http://10.42.0.1:7000/myBP_server/users/stealing_controller"
        payload = { 'station_id': str(station_id), 'place_id': str(place_id), 'status': str(status) }
        headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
        response = requests.post(url, data=json.dumps(payload), headers = headers)  
        return response

    def check_securityKey(self, place_id, station_id):
        url      = "http://10.42.0.1:7000/myBP_server/users/check_securityKey"
        payload = { 'station_id': str(station_id), 'place_id': str(place_id) }
        headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
        response = requests.post(url, data=json.dumps(payload), headers = headers)  
        return response
     
    def rqst_stop(self, status, place_id, station_id):  
        url      = "http://10.42.0.1:7000/myBP_server/users/stop_alarm"
        payload = { 'station_id': str(station_id), 'place_id': str(place_id), 'status': str(status) }
        headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
        response = requests.post(url, data=json.dumps(payload), headers = headers) 
        return response
        
    def update_dbServer(self, status,place_id, station_id):
        url     = "http://10.42.0.1:7000/myBP_server/users/update_dbServer"  
        payload = { 'station_id': str(station_id), 'place_id': str(place_id), 'status': str(status) }
        headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
        print payload
	response = requests.post(url, data=json.dumps(payload), headers = headers) 
        return response   
            