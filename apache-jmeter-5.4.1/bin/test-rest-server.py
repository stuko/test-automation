from flask import Flask, request, jsonify

app = Flask(__name__) 
@app.route('/', methods=['POST','GET']) 
def controller():
  params = request.get_json()
  print(params)
  return params

if __name__ == '__main__': 
  app.run(host="0.0.0.0",debug=True, port=8888)