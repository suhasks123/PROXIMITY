from django.db import models
from django.contrib.auth.models import User

class CurrentUser(models.Model):
    uid = models.CharField(max_length=100)
    interest = models.TextField()
    x = models.FloatField(default=0.0)
    y = models.FloatField(default=0.0)
    time = models.FloatField(default=0.0)

#class Blog(models.Model):
#    title = models.CharField(max_length=100)
#    added = models.DateTimeField(auto_now_add=True)
#    updated = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.uid
