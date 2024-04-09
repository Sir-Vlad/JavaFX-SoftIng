from flask import request, jsonify

from Backed import app


@app.route("/", methods=["POST"])
def entry_point():
    data = request.json

    return jsonify({"message": "Dati Ricevuti"}), 200


@app.route("/about", methods=["POST"])
def about():
    return jsonify({"result": "success"})
