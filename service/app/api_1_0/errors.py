# -*- coding: <utf-8> -*-
from flask import jsonify,make_response
from . import api
import json
from .encription import decode_json,encode_json

# 100 sign up success
# 101 login success
# 102 add todo success
# 601 user name is empty
# 602 password is empty
# 603 email is empry
# 701 user name is exists
# 702 email is exists
# 801 user is not exists
# 802 user doesn't add any todo
# 200 opration correct
# 401 request body is empty
# 403 unauthorized

def user_response(code,message):
	message = json.dumps(message)
  	message = encode_json(message,'123')
	return make_response(jsonify({'code':code,'result':message}),200)		

def response_right(code,message,key):
	message = json.dumps(message)
  	to_message = encode_json(message,'')
  	message = encode_json(message,key)
	return make_response(jsonify({'code':code,'result':message}),200)	