rm(list=ls())

data <- read.csv2('covid_dataset.csv',header=TRUE,sep=',',dec='.')

tests                   <-  data$Tests.1M.pop
cases                   <-  data$Tot.Cases.1M.pop
fatality_rate           <-  data$Case.Fatality.rate
GDPpercapita            <-  as.numeric(data$GDP)
PercentGDPforhealthexp  <-  as.numeric(data$GDP.for.health.expenditure)
IsinEU                  <-  ifelse(data$Country.Other%in%c('Austria','Belgium','Bulgaria','Croatia','Cyprus','Czechia',
                                                            'Denmark','Estonia','Finland','France','Greece','Germany','Hungary',
                                                            'Ireland','Italy','Latvia','Lithuania','Luxembourg','Malta','Netherlands',
                                                            'Poland','Portugal','Romania','Slovakia','Slovenia','Spain','Sweden'),1,0)
CO2emissionpercapita    <-  data$CO2.emission.per.capita
population              <-  data$Population
LifeExp                 <-  data$Life.Expectancy
deaths                  <-  data$Deaths.1M.pop
data                    <-  cbind(data,IsinEU)
govhealth               <-  as.numeric(data$Govhealthexp)

model = lm(log(tests)~log(cases)+fatality_rate+log(GDPpercapita)+PercentGDPforhealthexp+IsinEU+log(CO2emissionpercapita)+log(population)+LifeExp)
summary(model)

library(lmtest)
library(tseries)

#VISUALIZATIONS
library(ggplot2)

casestot        =   sum(na.omit(data$TotalCases+data$TotalRecovered))
deathstot       =   sum(na.omit(data$TotalDeaths))
mortality_rate  =   (deathstot/casestot)*100
mortality_rate2 =   mean(fatality_rate)

plot1   <- ggplot(data, aes(x=fatality_rate)) + geom_density(color='darkblue',fill='lightblue')
plot1+geom_vline(aes(xintercept=mortality_rate),
                 color="blue", linetype="dashed", size=1)+geom_vline(aes(xintercept=mortality_rate2),
                                                                     color="red", linetype="dashed", size=1)+labs(x='Stopa �miertelno�ci', y='G�sto��')

plot2   <- ggplot(data,aes(x=cases, y=deaths))+geom_point(color='red')+labs(x='Ilo�� przypadk�w na milion ludzi', y='Ilo�� �mierci na milion ludzi')+geom_vline(aes(xintercept=mean(na.omit(cases))),
                                                                                                                                                            color="red", linetype="dashed", size=1)
                                                                                                                                                            
plot3   <- ggplot(data,aes(x=tests,y=population))+geom_point(color='green') + labs(y='Populacja',x='Ilo�� test�w na milion mieszka�c�w')+geom_vline(aes(xintercept=mean(na.omit(tests))),color="green", linetype="dashed", size=1)
plot3

plot4   <- ggplot(data,aes(x=tests,y=GDPpercapita))+geom_point(color='green') + labs(y='PKB per capita',x='Ilo�� test�w na milion mieszka�c�w')+geom_vline(aes(xintercept=mean(na.omit(tests))),color="green", linetype="dashed", size=1)
plot4

plot5   <- ggplot(data,aes(x=tests,y=PercentGDPforhealthexp))+geom_point(color='green') + labs(y='% PKB na s�u�b� zdrowia',x='Ilo�� test�w na milion mieszka�c�w')+geom_vline(aes(xintercept=mean(na.omit(tests))),color="green", linetype="dashed", size=1)
plot5

plot6   <- ggplot(data,aes(x=tests,y=CO2emissionpercapita))+geom_point(color='green') + labs(y='Emisja CO2 per capita',x='Ilo�� test�w na milion mieszka�c�w')+geom_vline(aes(xintercept=mean(na.omit(tests))),color="green", linetype="dashed", size=1)
plot6

plot7   <- ggplot(data,aes(x=tests,y=LifeExp))+geom_point(color='green') + labs(y='�rednia d�ugo�� �ycia',x='Ilo�� test�w na milion mieszka�c�w')+geom_vline(aes(xintercept=mean(na.omit(tests))),color="green", linetype="dashed", size=1)
plot7

dataEU0 <- subset(data,IsinEU==0)
dataEU1 <- subset(data,IsinEU==1)
m1      <- mean(na.omit(dataEU0$Tests.1M.pop))
m2      <- mean(na.omit(dataEU1$Tests.1M.pop))
dataEU  <- data.frame(IsinEU=c(m2,m1), Value=c('Kraje UE', 'kraje spoza UE'))
plot8   <- ggplot(dataEU,aes(x=Value, y=IsinEU))+geom_bar(stat='identity', color='blue', fill='blue')+labs(x='',y='Ilo�� test�w na milion mieszka�c�w')
plot8

lastplot<-plot1<-ggplot(data, aes(x=tests)) + geom_density(color='darkblue',fill='lightblue') + labs(x='ilo�� test�w na milion mieszka�c�w')+geom_vline(aes(xintercept=mean(na.omit(tests))),color="blue", linetype="dashed", size=1)
lastplot

#TESTS

#RESET
reset(model)

#correlation matrix
library(corrplot)
datatest <- as.data.frame(cbind(cases,fatality_rate,GDPpercapita,PercentGDPforhealthexp,IsinEU,CO2emissionpercapita,population,LifeExp))
datatest[is.na(datatest)] <- 0
corrplot(model)

au <- as.matrix(cor(as.matrix(datatest)))
corrplot(au)

#VIF
library(AER)
vif(model)

#JB
res <- model$residuals
jarque.bera.test(res)
plot(res)

#heteroskedasticity
gqtest(model)
bptest(model)

modeltest <- lm(log(tests)~log(cases)+I(log(cases)^2)+fatality_rate+I(fatality_rate^2)+log(GDPpercapita)+I(log(GDPpercapita)^2)+PercentGDPforhealthexp+I(PercentGDPforhealthexp^2)+IsinEU+log(CO2emissionpercapita)+I(log(CO2emissionpercapita)^2)+log(population)+I(log(population)^2)+LifeExp+I(LifeExp^2))
LM_white <- nrow(data)*summary(modeltest)$r.squared
pchisq(LM_white,modeltest$rank-1,lower.tail = F)

VCOVHC0 <- vcovHC(model,type="HC0")
coeftest(model, vcov.=VCOVHC0)

weights = 1/GDPpercapita
modeltest2 <- lm(log(tests)~log(cases)+fatality_rate+log(GDPpercapita)+PercentGDPforhealthexp+IsinEU+log(CO2emissionpercapita)+log(population)+LifeExp,weights=weights)
summary(modeltest2)

#autocorrelation
dwtest(model)
Box.test(res,lag=4,type=c("Ljung-Box"))
bgtest(model,order=4)

#endogeneity
IV <- ivreg(log(tests)~log(cases)+fatality_rate+log(GDPpercapita)+PercentGDPforhealthexp+IsinEU+log(CO2emissionpercapita)+log(population)+LifeExp|govhealth+LifeExp)
summary(IV,diagnostics=T)

#traceability
modelid = lm(LifeExp~PercentGDPforhealthexp+govhealth+fatality_rate+tests)
summary(modelid)

library(systemfit)

eqT <- log(tests) ~ log(cases) + fatality_rate + log(GDPpercapita) + PercentGDPforhealthexp + IsinEU + log(CO2emissionpercapita) + log(population) + LifeExp
eqH <- LifeExp ~ PercentGDPforhealthexp + govhealth + fatality_rate + tests

system <- list(tests = eqT, health = eqH)

inst <-~log(cases)+fatality_rate+log(GDPpercapita)+PercentGDPforhealthexp+IsinEU+log(CO2emissionpercapita)+log(population)+govhealth
system2SLS = systemfit(system,"2SLS",inst=inst)
summary(system2SLS)