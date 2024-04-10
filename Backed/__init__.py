from flask import Flask
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///site.db"
db = SQLAlchemy(app)

from Backed import routes

with app.app_context():
    # creare tutte gli schemi delle tabelle che non esistono
    db.create_all()
