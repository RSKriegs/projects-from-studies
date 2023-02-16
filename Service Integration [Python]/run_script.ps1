python -m venv venv
venv\scripts\activate
pip install -r requirements.txt
cd mysite
python manage.py makemigrations
python manage.py migrate
python manage.py migrate --run-syncdb
python manage.py runserver