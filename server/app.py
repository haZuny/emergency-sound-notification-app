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
        
        return render_template('index.html', time = now, dataList = db_list)
    
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
    
    
###
# False Value Rauting
###
@app.route('/false-val/', methods=['GET', 'POST'])
def false_val():
    
    # GET, 데이터 인덱스 접근
    if request.method == 'GET':
        return jsonify(400, {'state':'error', 'msg':'Body contents are not match with this server.'})
    
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
    
    
    
###
# Run Falsk Server
###
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=4000)