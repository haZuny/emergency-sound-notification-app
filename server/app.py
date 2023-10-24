from flask import Flask, request, jsonify  , render_template
import sqlite3
from datetime import datetime

# Create SQLite Table when first time
try:
    sqlite_connect = sqlite3.connect('sound.db')
    sqlite_connect.execute('create table true (id integer primary key, category text, percent_carhorn real, percent_dogbark real, percent_siren real, percent_none real, sound_buf text, datetime text)')
    sqlite_connect.execute('create table false (id integer primary key, category text, percent_carhorn real, percent_dogbark real, percent_siren real, percent_none real, sound_buf text, datetime text)')
    sqlite_connect.close()
except:
    pass

# Get Flask Obj
app = Flask(__name__)

###
# True Value Rauting
###
@app.route('/true-val/', methods=['GET', 'POST'])
def true_val():
    
    # GET, 데이터 인덱스 접근
    if request.method == 'GET':
        # Get current time
        now = str(datetime.now())[:16]
        
        # Get DB
        sqlite_connect = sqlite3.connect('sound.db')
        sqlite_cursor = sqlite_connect.cursor()
        sqlite_cursor.execute("SELECT id, category, datetime FROM true")
        db_list = sqlite_cursor.fetchall()
        
        return render_template('index.html',  type = "useful", time = now, dataList = db_list)
    
    # POST, 데이터 저장
    if request.method == 'POST':
        # Get Json
        try:
            data = request.get_json()
            id = int(data['id'])
            category = data['category']
            percent_carhorn = float(data['percent_carhorn'])
            percent_dogbark = float(data['percent_dogbark'])
            percent_siren = float(data['percent_siren'])
            percent_none = float(data['percent_none'])
            sound_buf = data['sound_buf']
            dateTime = data['datetime']
        except:
            return jsonify(400, {'state':'error', 'msg':'Body contents are not match with this server.'})
        
        # Insert into sqlite
        try:
            sqlite_connect = sqlite3.connect('sound.db')
            sqlite_cursor = sqlite_connect.cursor()
            sqlite_cursor.execute('insert into true values(?, ?, ?, ?, ?, ?, ?, ?);', (id, category, percent_carhorn, percent_dogbark, percent_siren, percent_none, sound_buf, dateTime))
            sqlite_connect.commit()
            sqlite_connect.close()
        except Exception as e:
            print('Error:\t', e)
            sqlite_connect.close()
            return jsonify(500, {'state':'error', 'msg':'Error occured while saving data.'})
        
        # Save with SQLite
        return jsonify({'state':'success', 'msg':"Successfully save data"}, 200)
    
@app.route('/true-val/<id>/', methods=['GET'])
def true_val_data(id):
    
    # GET, 데이터 인덱스 접근
    if request.method == 'GET': 
        # Get current time
        now = str(datetime.now())[:16]
             
        # Get DB
        sqlite_connect = sqlite3.connect('sound.db')
        sqlite_cursor = sqlite_connect.cursor()
        sqlite_cursor.execute("SELECT * FROM true where id = " + str(id))
        db_list = sqlite_cursor.fetchall()
        
        return render_template('detail.html',nowTime = now, category = db_list[0][1], percent_carHorn = f"{db_list[0][2]:.4f}", percent_dogBark = f"{db_list[0][3]:.4f}", percent_siren= f"{db_list[0][4]:.4f}", percent_none= f"{db_list[0][5]:.4f}", datetime=db_list[0][7])
    
    
    
###
# False Value Rauting
###
@app.route('/false-val/', methods=['GET', 'POST'])
def false_val():
    
    # GET, 데이터 인덱스 접근
    if request.method == 'GET':
        # Get current time
        now = str(datetime.now())[:16]
        
        # Get DB
        sqlite_connect = sqlite3.connect('sound.db')
        sqlite_cursor = sqlite_connect.cursor()
        sqlite_cursor.execute("SELECT id, category, datetime FROM false")
        db_list = sqlite_cursor.fetchall()
        
        return render_template('index.html', type = "unuseful", time = now, dataList = db_list)
    
    # POST, 데이터 저장
    if request.method == 'POST':
        # Get Json
        try:
            data = request.get_json()
            id = int(data['id'])
            category = data['category']
            percent_carhorn = float(data['percent_carhorn'])
            percent_dogbark = float(data['percent_dogbark'])
            percent_siren = float(data['percent_siren'])
            percent_none = float(data['percent_none'])
            sound_buf = data['sound_buf']
            dateTime = data['datetime']
        except:
            # 바디 인스턴스 에러
            return jsonify(400, {'state':'error', 'msg':'Body contents are not match with this server.'})
        
        # Insert into sqlite
        try:
            sqlite_connect = sqlite3.connect('sound.db')
            sqlite_cursor = sqlite_connect.cursor()
            sqlite_cursor.execute('insert into false values(?, ?, ?, ?, ?, ?, ?, ?);', (id, category, percent_carhorn, percent_dogbark, percent_siren, percent_none, sound_buf, dateTime))
            sqlite_connect.commit()
            sqlite_connect.close()
        except Exception as e:
            print('Error:\t', e)
            sqlite_connect.close()
            return jsonify(500, {'state':'error', 'msg':'Error occured while saving data.'})
        
        # Save with SQLite
        return jsonify({'state':'success', 'msg':"Successfully save data"}, 200)

@app.route('/false-val/<id>/', methods=['GET'])
def false_val_data(id):
    
    # GET, 데이터 인덱스 접근
    if request.method == 'GET': 
        # Get current time
        now = str(datetime.now())[:16]
             
        # Get DB
        sqlite_connect = sqlite3.connect('sound.db')
        sqlite_cursor = sqlite_connect.cursor()
        sqlite_cursor.execute("SELECT * FROM false where id = " + str(id))
        db_list = sqlite_cursor.fetchall()
        
        return render_template('detail.html',nowTime = now, category = db_list[0][1], percent_carHorn = f"{db_list[0][2]:.4f}", percent_dogBark = f"{db_list[0][3]:.4f}", percent_siren= f"{db_list[0][4]:.4f}", percent_none= f"{db_list[0][5]:.4f}", datetime=db_list[0][7])
    
    
###
# Run Falsk Server
###
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=4000)