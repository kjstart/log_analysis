import os
import sys
# 数据预处理以及绘制图形需要的模块
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
##
from sklearn.preprocessing import MinMaxScaler

from keras.models import Sequential

from keras.layers import Dense, LSTM, BatchNormalization

###############################################################################

def sotck_dataset():
    # 从csv读取数据
    dataset = pd.read_csv('/Users/lialiu/Downloads/log_2.csv', encoding='utf-8')
    # print(dataset)
    # 数据集的维度
    print(dataset.shape)
    # 转换为整数
    dataset['value'] = dataset['value'].astype(int)
    # 列名选取行
    dataframeDataset = dataset.loc[0:, ['value']]
    # 按时间反转下数据
    # dataframeDatasetReverse = dataframeDataset.reindex(index=dataframeDataset.index[::-1])
    # python list
    listDataset = dataframeDataset.values

    return dataset.loc[0:, ['log']], listDataset


"""
归一化函数
"""


def sc_fit_transform(nDlist):
    # 将所有数据归一化为0-1的范围
    sc = MinMaxScaler(feature_range=(0, 1))
    '''
    fit_transform()对部分数据先拟合fit，
    找到该part的整体指标，如均值、方差、最大值最小值等等（根据具体转换的目的），
    然后对该trainData进行转换transform，从而实现数据的标准化、归一化等等。
    '''
    dataset_transform = sc.fit_transform(X=nDlist)
    # 归一化后的数据
    return sc, np.array(dataset_transform)


"""
反归一化函数
"""


def sc_inverse_transform(sc, nDlist):
    return scTestDataseY.inverse_transform(X=nDlist)


###############################################################################
# 需要之前几次的数据来预测下一次的数据，
need_num = 10
# 训练数据的大小
training_num = 5000
# 测试数据的大小
test_num = 100
# 迭代训练10次
epoch = 10
# 每次取数据数量
batch_size = 10
###############################################################################
# 数据处理
# 获取数据
logIds, listDataset = sotck_dataset()
# 生成训练数据集
xTrainDataset = []
xTrainDataset = logIds[0:training_num]
# 对应的系统资源消耗
# yTrainDataset = listDataset[1:training_num]
yTrainDataset = logIds[1:training_num + 1]
# print(xTrainDataset)
# print(yTrainDataset)
###############################################################################
# 原始数据归一化
# 转换位n行1列的二维数组
xTrainDataset = np.array(xTrainDataset)
xTrainDataset = xTrainDataset.reshape(-1, 1)
scTrainDataseX, xTrainDataset = sc_fit_transform(xTrainDataset)

yTrainDataset = np.array(yTrainDataset)
yTrainDataset = yTrainDataset.reshape(-1, 1)
scTrainDataseY, yTrainDataset = sc_fit_transform(yTrainDataset)
print(xTrainDataset.shape)
print(yTrainDataset.shape)

###############################################################################
# 生成lstm模型需要的训练集数据和
xTrain = []
for i in range(need_num, training_num):
    xTrain.append(xTrainDataset[i - need_num:i])
xTrain = np.array(xTrain)
# print(xTrain)
print(xTrain.shape)
# 因为LSTM要求输入的数据格式为三维的，[training_number, time_steps, 1]，因此对数据进行相应转化
xTrain = np.reshape(xTrain, (xTrain.shape[0], xTrain.shape[1], 1))
# print(xTrain)
print(xTrain.shape)

yTrain = []
for i in range(need_num, training_num):
    yTrain.append(yTrainDataset[i - 1])
yTrain = np.array(yTrain)
# print(yTrain)
print(yTrain.shape)
###############################################################################
# 构建网络，使用的是序贯模型
model = Sequential()
# return_sequences=True返回的是全部输出，LSTM做第一层时，需要指定输入shape

model.add(LSTM(units=128, return_sequences=True, input_shape=[xTrain.shape[1], 1]))
# model.add(BatchNormalization())

model.add(LSTM(units=128))
# model.add(BatchNormalization())

model.add(Dense(units=1))
# 进行配置
model.compile(optimizer='adam',
              loss='mean_squared_error')

model.fit(x=xTrain, y=yTrain, epochs=epoch, batch_size=batch_size)
###############################################################################
# 进行测试数据的处理
# 生成测试数据集
xTestDataset = []
xTestDataset = logIds[training_num:training_num + test_num]
# 系统资源消耗
yTestDataset = []
yTestDataset = logIds[training_num + 1:training_num + test_num + 1]
# 测试集数据归一化
xTestDataset = np.array(xTestDataset)
xTestDataset = xTestDataset.reshape(-1, 1)
scTesDatasetX, xTestDataset = sc_fit_transform(xTestDataset)

yTestDataset = np.array(yTestDataset)
yTestDataset = yTestDataset.reshape(-1, 1)
scTestDataseY, yTestDataset = sc_fit_transform(yTestDataset)
# print(xTestDataset.shape)
# print(yTestDataset.shape)
# 因为LSTM要求输入的数据格式为三维的，[training_number, time_steps, 1]，因此对数据进行相应转化
# 生成lstm模型需要的训练集数据和
xTest = []
for i in range(need_num, test_num):
    xTest.append(xTestDataset[i - need_num:i])
xTest = np.array(xTest)
# print(xTrain)
print(xTest.shape)
# 因为LSTM要求输入的数据格式为三维的，[training_number, time_steps, 1]，因此对数据进行相应转化
xTest = np.reshape(xTest, (xTest.shape[0], xTest.shape[1], 1))
# print(xTrain)
print(xTest.shape)

yTest = []
for i in range(need_num, test_num):
    yTest.append(yTestDataset[i - 1])
yTest = sc_inverse_transform(scTestDataseY, yTest)
# print(yTrain)
print(yTest.shape)
###############################################################################
# 进行预测
yPredictes = model.predict(x=xTest)
# print(yPredictes)
yPredictes = scTestDataseY.inverse_transform(X=yPredictes)
print(yPredictes)
plt.plot(yTest, color='red', label='Real Network')
plt.plot(yPredictes, color='blue', label='Predicted Network')
plt.title(label='System Resource Prediction')
plt.xlabel(xlabel='Time')
plt.ylabel(ylabel='Network Outbound')
plt.legend()
plt.show()
