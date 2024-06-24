python manage.py makemigrations
python manage.py migrate

python manage.py loaddata ./dati_templete/utenti.json
python manage.py loaddata ./dati_templete/concessionari.json
python manage.py loaddata ./dati_templete/optional.json
python manage.py load_modelliAuto ./dati_templete/modelliAuto.json
python manage.py loaddata ./dati_templete/imgAutoNuove.json

python manage.py month_on_periodo
python manage.py create_sconti giugno 2024 6