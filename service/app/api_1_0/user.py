# -*- coding: <utf-8> -*-
import time
from . import api
from flask import request,make_response,abort,jsonify
from .errors import user_response
from .. import db
import json
from .encription import decode_json,encode_json
from passlib.apps import custom_app_context as pwd_context
from itsdangerous import (TimedJSONWebSignatureSerializer
                          as Serializer, BadSignature, SignatureExpired)

class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(64),unique=True,index=True)
    username = db.Column(db.String(64), unique=True, index=True)
    password_hash = db.Column(db.String(128))
    device_id = db.Column(db.String(64))
    device_name = db.Column(db.String(64))
    sign_time = db.Column(db.Integer)
    def hash_password(self,password):
        self.password_hash = pwd_context.encrypt(password,self.password_hash)

    def verify_password(self,password):
        return pwd_context.verify(password, self.password_hash)

    def generate_auth_token(self,expiration=3600*24*365):
    	s = Serializer('jlfdasjkhkdfaj',expires_in=expiration)
    	return s.dumps({'id':self.id})

    @staticmethod    
    def verify_auth_token(token):
       	s = Serializer('jlfdasjkhkdfaj')
        print s
       	try:
       		data = s.loads(token)
       	except SignatureExpired:
       		return None
       	except BadSignature:
       		return None
        print data
        if data.get('id') is None:
          return None
       	user = User.query.get(data['id'])
        print user
       	return user

    @staticmethod
    def to_json(self,token=''):
      user = {
        'id':self.id,
        'username':self.username,
        'token':token
      }
      print user
      return user

    def __repr__(self):
        return '<User %r,%d>' % (self.username,self.id)


def add_user(json_user):
  origin = decode_json(json_user,'123')
  # origin = decode_json(json_user,'')
  json_origin = json.loads(origin)  
  username = json_origin.get("username")
  password = json_origin.get("password")
  email = json_origin.get("email")
  device_id = json_origin.get("device_id")
  device_name = json_origin.get("device_name")
  sign_time = int(time.time())
  user = User()

  if username is None or len(username) == 0:
    message = " user name is empty"
    return user_response(601,message)

  if password is None or len(password) == 0:
    message = "passwod is empty" 
    return user_response(602,message)    
    
  if email is None or len(email) == 0:
    message = "email is empty" 
    return user_response(603,message)    


  if User.query.filter_by(username=username).first() is not None:
    message = "user name is exists" 
    return user_response(701,message)

  if User.query.filter_by(email=email).first() is not None:
    message = "email  is exists" 
    return user_response(702,message)

  user.username = username
  user.hash_password(password)
  user.email = email
  user.device_id = device_id
  user.device_name = device_name
  user.sign_time = sign_time
  token = user.generate_auth_token()

  db.session.add(user)
  db.session.commit() 
  return user_response(100,User.to_json(user,token))
	

def get_user(json_user):
  origin = decode_json(json_user,'123')
  json_origin = json.loads(origin)  
  username = json_origin.get("username")
  password = json_origin.get("password")  

  if username is None or len(username) == 0:
    message = " user name is empty"
    return user_response(601,message)

  if password is None or len(password) == 0:
    message = "passwod is empty" 
    return user_response(602,message)    

  print ("user name" ,username  )
  userform = User.query.filter_by(username = username)  
  if userform.first() is None:
    userform = User.query.filter_by(email = username)
    
  user = userform.first()
  if user is None:
    return user_response(801,"user is not exists")  

  if not user.verify_password(password):
  	return user_response(603,"wrong password")  

  token = user.generate_auth_token()
  return user_response(101,User.to_json(user,token))
