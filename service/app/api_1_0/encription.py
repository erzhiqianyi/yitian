# -*- coding: <utf-8> -*-
def encode_json(origin, key):
	origin_bytes = bytearray(origin,encoding='utf-8')
	key_bytes = bytearray(key,encoding='utf-8')
	for i in xrange(0,len(origin_bytes)):
		for j in xrange(0,len(key_bytes)):
			origin_bytes[i] = origin_bytes[i]^key_bytes[j] 
	return [i for i in origin_bytes]
	
def decode_json(origin_byte, key):
	for x in xrange(0,len(origin_byte)):
		if origin_byte[x] < 0:
			origin_byte[x] = 256 + origin_byte[x]
			
	key_bytes = bytearray(key,encoding='utf-8')
	dee = origin_byte
	for i in xrange(0,len(origin_byte)):
		for j in xrange(0,len(key_bytes)):
			origin_byte[i] = dee[i]^key_bytes[j] 
	b = bytearray(origin_byte)
	return str(b)

	
