# -*- coding: <utf-8> -*-
import os
basedir = os.path.abspath(os.path.dirname(__file__))


class Config:
    SECRET_KEY = os.environ.get('SECRET_KEY') or 'how to generate a random string '
    SQLALCHEMY_COMMIT_ON_TEARDOWN = True

    @staticmethod
    def init_app(app):
        app.debug=True


class DevelopmentConfig(Config):
    DEGUB = True
    host = '0.0.0.0'
    SQLALCHEMY_DATABASE_URI = 'mysql://root:zxx6108fc@localhost/yitian?charset=utf8mb4'
 
class TestingConfig(Config):
    TESTING = True
    SQLALCHEMY_DATABASE_URI = 'mysql://root:zxx6108fc@localhost/yitian_test'   
    


class ProductionConfig(Config):
      SQLALCHEMY_DATABASE_URI = 'mysql://root:zxx6108fc@localhost/yitian'

config = {
    'development': DevelopmentConfig,
    'testing': TestingConfig,
    'production': ProductionConfig,

    'default': DevelopmentConfig
}
