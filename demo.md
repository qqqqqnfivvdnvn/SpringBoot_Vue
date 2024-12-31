# Demo
创建Vue项目
vue create vue_demo

# 在本地项目目录中初始化Git仓库（如果尚未这样做）  git branch -M main
git init

# 添加所有文件到Git仓库 
git add .


# 提交更改到本地仓库
git commit -m "Initial commit"

# 添加GitHub仓库作为远程仓库（将'your-repository-url'替换为GitHub仓库的URL）git remote remove origin    git remote -v

git remote add origin https://github.com/qqqqqnfivvdnvn/SpringBoot_Vue.git

# 将更改推送到GitHub仓库，这将创建新分支并推送更改
git push origin master

