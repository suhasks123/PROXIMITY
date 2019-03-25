from django.shortcuts import render
from django.http import HttpResponse
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import CurrentUser

import json

from fuzzywuzzy import fuzz
from fuzzywuzzy import process


#def home(request):
#    return render(request, 'proximity/home.html')

#def about(request):
#    return HttpResponse('<h1>About the App</h1>')

#def json_parse(j):
#    cuser = json.loads(j)

timesorted = []
locationsorted = []
cuser = []
p_user = []
p_int = []
data = []


#cuser is the dictionary obtained from the json file
#the following data is the dummy data to just enable the views.py to compile without errors till we integrate json
#cuser = json.loads(request.body)
@csrf_exempt
def main(request):
    if request.method == "POST":
        cuser = json.loads(request.body)
    #x='{"uid":"ryuhsirjshasdbbrus","interest":"sports;art;music;drama","x":"74.78929","y":"13.24532","time":"15.27"}'
    #cuser = json.loads(x)
    cuser['x'] = float(cuser['x'])
    cuser['y'] = float(cuser['y'])
    cuser['time'] = float(cuser['time'])
    update(cuser)
    location(cuser)
    interest(cuser)
    return JsonResponse(data, safe=False)
    #return HttpResponse(data)


#cuser={
#    'uid':'asdasdasd',
#    'interest':'kjdfskjdfhsjdf',
#    'x':829.283724,
#    'y':230.893124,
#}

#update - for every search request to the backend, it updates the location and time of an existing user or
# creates a new query if the user requesting is new
def update(cuser):
    global timesorted
    flag = 0
    i=0
    currentuser_set = CurrentUser.objects.all()
    for currentuser in currentuser_set:
        if(currentuser.uid==cuser['uid']):
            #CurrentUser.objects.get(x=cuser['x'],
            #                            y=cuser['y'],
            #                            time=cuser['time'])
            currentuser.x = cuser['x']
            currentuser.y = cuser['y']
            currentuser.time = cuser['time']
            flag = 1

        if(cuser['time']-currentuser.time<=0.30):
                    timesorted.append(currentuser.uid)
                    i+=1
    if(flag==0):
        CurrentUser.objects.create(uid=cuser['uid'],
                                interest=cuser['interest'],
                                x=cuser['x'],
                                y=cuser['y'],
                                time=cuser['time'])
    return


#time - filters the whole database based on the recorded time of the previous request by all users in the database
#to save computation and time, we have shifted this database looping to the previous update method
#hence update and time based filtering can be done in the same method

#def time():
#    currentuser_set = CurrentUser.objects.all()
#    i=0
#    for currentuser in currentuser_set:
#        if((cuser['time'].hour-currentuser.time.hour)==0 and (cuser['time'].minute-currentuser.time.minute)<=30):
#            timesorted[i]=currentuser.uid
#        i+=1
#    location()


#location - it takes the list 'timesorted' and filters it further by their nearness to the current user
def location(cuser):
    i=0
    d=0.007
    global locationsorted
    rpx=cuser['x']+d
    rnx=cuser['x']-d
    rpy=cuser['y']+d
    rny=cuser['y']-d
    for user in timesorted:
        currentuser = CurrentUser.objects.filter(uid=user).first()
        #for u in currentuser:
        #    if((u.x<=rpx and u.x>=rnx) and (u.y<=rpy and u.y>=rny)):
        #        locationsorted.append(u.uid)
        if((currentuser.x<=rpx and currentuser.x>=rnx) and (currentuser.y<=rpy and currentuser.y>=rny)):
            locationsorted.append(user)
        i+=1
    return


def interest(cuser):
    global locationsorted
    global p_user
    global p_int
    global data
    for user in locationsorted:
        p_user.append(user)
        p_int.append(CurrentUser.objects.filter(uid=user).first().interest)
    rank(CurrentUser.objects.filter(uid=cuser['uid']).first().interest,p_user,p_int)
    return


#INTEREST MATCHING STARTS HERE-----------------------------------------------

i1=[]
i2=[]
c=[]

#rank receives two parameters:
#1. u_int, which is a string with the current users interests
#2. users, the list with the uid of the people nearby
#3. intval ,the list with interest strings of people nearby
#this dictionary (p_int) is converted to two lists for easy use

def rank(u_int,users,intval):
    global data
    global p_user
    global p_int
    ctr=0
    p=len(users)
    r=[]
    #print(users)
    #print(intval)
#    for x,y in p_int.items():
#        intval[ctr]=y
#        users[ctr]=x
#        ctr+=1
    ctr=0
    for x in intval:
        r.append(compare(u_int,intval[ctr]))
        ctr+=1
    #the following for loop sorts the list of users based on the number of common interests they have with the current user
    #for i in range(p):
    #    for j in range(0,p-1-i):
    #        if(r[j]<r[j+1]):
    #            temp=r[j]
    #            r[j]=r[j+1]
    #            r[j+1]=temp
#
#                temp=users[j]
#                users[j]=users[j+1]
#                users[j+1]=temp
#
#                temp=intval[j]
#                intval[j]=intval[j+1]
#                intval[j+1]=temp
    ctr=0
    #for ctr in range(p-2):
    #data=dict(zip(users,r))
    for u in users:
        data.append({'uid':u})
    return


#    for ctr in range(p):
#        print(r[ctr],". ",users[ctr]," - ",intval[ctr])

#*******HERE WE NEED TO SEND THE DICTIONARY OF COMMON INTEREST TO UID TO THE FRONTEND*********


#compare - compares the interest strings by organizing them into lists and then using fuzzy logic to compare each interest

def compare(interest1, interest2):
    global i1, i2
    organize(interest1, i1)
    organize(interest2, i2)
    ctr=0
    cmn=0
    n=0
    i=0
    for s1 in i1:
        for s2 in i2:
            n=fuzz.ratio(s1,s2)
            #print(s1," and ",s2," = ",n)
            if(n>=60):
                cmn+=1
                i+=1
    return cmn

#organize - divides the interest string into a list of seperate interests

def organize(str,arr):
    s=''
    ctr=0
    for l in str:
        if(l==';'):
            ctr+=1
            continue
        s += l
        arr.append(s)
