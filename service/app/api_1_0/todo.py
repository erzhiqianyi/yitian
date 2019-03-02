# -*- coding: <utf-8> -*-
import time
import copy
from . import api
from flask import request,make_response,abort,jsonify
from .errors import response_right
from .. import db
import json
from .encription import decode_json,encode_json


class Todo(db.Model):
    __tablename__ = 'todo'
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.Integer)
    todo_id = db.Column(db.Integer)
    start_time = db.Column(db.BigInteger)
    end_time = db.Column(db.BigInteger)
    tag = db.Column(db.Text)
    description = db.Column(db.Text)
    location = db.Column(db.Text)
    level = db.Column(db.Integer)
    day = db.Column(db.Integer)

    @staticmethod
    def from_json(json_todo):
        todo_id = json_todo.get('todo_id')
        user_id = json_todo.get('user_id')
        start_time = json_todo.get('start_time')
        end_time = json_todo.get('end_time')
        tag = json_todo.get('tag')
        description = json_todo.get('description')
        location = json_todo.get('location')
        level = json_todo.get('level')
        day = json_todo.get('day')

        if todo_id is None or user_id is None or start_time is None or end_time is None:
            return None

        todo = Todo()
        todo.todo_id = todo_id
        todo.user_id = user_id
        todo.start_time = start_time
        todo.end_time = end_time
        todo.tag = tag
        todo.description = description
        todo.location = location
        todo.level = level
        todo.day = day

        return todo

    @staticmethod
    def to_json(self):
        todo = {
            'todo_id':self.todo_id,
            'user_id':self.user_id,
            'start_time':self.start_time,
            'end_time':self.end_time,
            'tag':self.tag,
            'description':self.description,
            'location':self.location,
            'level':self.level,
            'day':self.day

        }

        return todo

    def __repr__(self):
        return '<User %r,%d>' % (self.user_id,self.todo_id)

def add_todos(json_todo):
    todos_decoded = decode_json(json_todo,'123')
    todos_array = json.loads(todos_decoded)
    todos = todos_array.get('todos')
    todos = [Todo.from_json(todo) for todo in todos]

    print len(todos)
    for todo in todos:
        if todo is not None:
            db.session.add(todo)
    db.session.commit()

    return response_right(201,'add todos success','123')

def get_todos(user_id,day=None):
    print user_id,day 
    if day is None:
        todos = Todo.query.filter_by(user_id = user_id).all()    
    else:
        todos = Todo.query.filter_by(user_id = user_id,day=day).all()        
        
    
    if todos is None:
        return response_right(802,'no todo','')
    result = [Todo.to_json(todo) for todo in todos]
    result = {'todos':result}

    print result
    return response_right(201,result,'123')

def update_todos(json_new_todo):
    todos_decoded = decode_json(json_new_todo,'123')    
    todos_array = json.loads(todos_decoded)
    todos_array = todos_array.get('todos')

    todos_new = [Todo.from_json(todo) for todo in todos_array]
	
    for todo in todos_new:
        print todo 
        if todo is not None:
	        todo_in_db = Todo.query.filter_by(user_id=todo.user_id,todo_id=todo.todo_id).first()
	        if todo_in_db is not None:
	        	todo_in_db.start_time = todo.start_time 
		        todo_in_db.end_time = todo.end_time 
		        todo_in_db.tag = todo.tag
		        todo_in_db.description = todo.description
	        	todo_in_db.level = todo.level
	        	todo_in_db.location = todo.location
	        	todo_in_db.day = todo.day
	        	print Todo.to_json(todo_in_db)       
	        	db.session.add(todo_in_db )
	db.session.commit

    return response_right(201,'update success','123')

def delete_todo(user_id):
    todos = Todo.query.filter_by(user_id = user_id).all()
    for todo in todos:
        db.session.delete(todo)
    db.session.commit()
    return  response_right(201,'delete success','123')
