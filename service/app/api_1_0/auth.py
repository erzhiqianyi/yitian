# -*- coding: <utf-8> -*-
import time
from . import api
from .encription import encode_json,decode_json
from flask import request,make_response,abort,jsonify,g
from .user import User,add_user ,get_user
from .todo import Todo ,add_todos,get_todos,update_todos,delete_todo
from .. import db
from flask.ext.httpauth import HTTPBasicAuth
import json as json_tool
from .errors import response_right

auth = HTTPBasicAuth()

@auth.verify_password
def verify_token(token,password=''):
	# first try to authentcate by token 
	user = User.verify_auth_token(token)
	print 'user is None',user is None
	if user is None:
		return False
	g.user = user
	return True

@auth.error_handler
def unauthorized():
    return response_right(401,"not login",'fdsafdsadgrew')

@api.route('/users',methods=['POST'])
def new_user():
	json = request.json
	if json is None or len(json) == 0 :
		return response_right(401,"request body is empty",'fsajfghkdjashg')
	return add_user(json)
	

@api.route('/user',methods=['POST'])
def login():
	json = request.json
	if json is None or len(json) == 0 :
		return response_right(401,"request body is empty",'aghaksdhsdjklahfklsadhfkjlsdah')
	return get_user(json)		

@api.route('/todos/all',methods=['GET'])
@auth.login_required
def get_todo():
	id = g.user.id	
	# id = 1
	return get_todos(id)

@api.route('/todos/<day>',methods=['GET'])
@auth.login_required
def get_todo_by_day(day):
	id = g.user.id	
	# id = 1
	return get_todos(id,day)

@api.route('/todos',methods=['POST'])
@auth.login_required
def new_todo():
	json = request.json
	if json is None or len(json) == 0 :
		return response_right(401,"request body is empty",'jhflkd')
	return add_todos(json)

@api.route('/todos',methods=['PUT'])
@auth.login_required
def update():
	json = request.json
	if json is None or len(json) == 0 :
		return response_right(401,"request body is empty",'hkkfsa')
	return update_todos(json)



@api.route('/todos',methods=['DELETE'])
@auth.login_required
def delete():
	id = g.user.id	
	# id = 2
	return delete_todo(id)

@api.route('/version',methods=['GET'])
def getNewVersion():
	version = {
		'version':1,
		'name':'yitian',
		'desc':'original version',
		'url':'http://www.wandoujia.com/apps/io.github.buniaowanfeng'
	}
	return make_response(jsonify(version))