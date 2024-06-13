cd $(pwd)

python manage.py loaddata ./dati_template/optional.json;
python manage.py loaddata ./dati_template/concessionari.json;
python manage.py loaddata ./dati_template/utenti.json;
python manage.py load_modelliAuto ./dati_template/modelliAuto.json;
python manage.py loaddata ./dati_template/imgAutoNuove.json