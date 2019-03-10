from fuzzywuzzy import fuzz
from fuzzywuzzy import process


i1=['','','','']
i2=['','','','']
c=['','','','']


def rank(u_int,p_int):
    ctr=0
    p=len(p_int)
    intval=[]
    users=[]
    r=[]
    intval=list(p_int.values())
    users=list(p_int.keys())
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
    for i in range(p):
        for j in range(0,p-1-i):
            if(r[j]<r[j+1]):
                temp=r[j]
                r[j]=r[j+1]
                r[j+1]=temp

                temp=users[j]
                users[j]=users[j+1]
                users[j+1]=temp

                temp=intval[j]
                intval[j]=intval[j+1]
                intval[j+1]=temp
    ctr=0
    for ctr in range(p):
        print(r[ctr],". ",users[ctr]," - ",intval[ctr])




def compare(interest1, interest2):
    i1=['','','','']
    i2=['','','','']
    c=['','','','']
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
                c[i]=s1
                cmn+=1
    return cmn



def organize(str, arr):
    ctr=0
    for l in str:
        if(l==';'):
            ctr+=1
            continue
        arr[ctr]+=l
