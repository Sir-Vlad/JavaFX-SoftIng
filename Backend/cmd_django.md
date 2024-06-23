# Comandi Django

## Lanciare il server

```python
python manage.py runserver
```

## Creare il database

```python
python manage.py makemigrations
```

```python
python manage.py migrate
```

## Caricare i dati di template nel database

```python
python manage.py loaddata <file.json>
```

## Salvare i dati di template del database

```python
python manage.py dumpdata Backend_IngSoft.<tabella> --indent=4 --output=output.json
```

## Reset database

```python
python manage.py reset_db
```

## Creare un superuser

```python
python manage.py createsuperuser
```

## Creare il grafico del database

```python
python manage.py graph_models -a -o graph_db.svg
```
