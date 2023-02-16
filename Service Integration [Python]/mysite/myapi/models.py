#models.py
from django.db import models
from django.core.validators import RegexValidator

letters_only = RegexValidator(r'^[a-zA-Z]*$', 'Only letters are allowed for that field')
numbers_only = RegexValidator(r'^[0-9]*$', 'Only numbers are allowed for that field')

class Author(models.Model):
    id = models.AutoField(primary_key=True)
    first_name = models.CharField(max_length=60,blank=True,null=True,validators=[letters_only])
    last_name = models.CharField(max_length=60,blank=True,null=True,validators=[letters_only])
    description = models.CharField(max_length=1000,blank=True,null=True)

    def __str__(self):
        return "%s %s" % (self.first_name, self.last_name)

class Quote(models.Model):
    id = models.AutoField(primary_key=True)
    content = models.CharField(max_length=1000)
    author = models.ForeignKey(Author, on_delete=models.SET_NULL,related_name='quotes',blank=True,null=True)
    source = models.CharField(max_length=60,blank=True,null=True)
    context = models.CharField(max_length=1000,blank=True,null=True)
    year = models.IntegerField(blank=True,null=True,validators=[numbers_only])

    def __str__(self):
        return self.content
