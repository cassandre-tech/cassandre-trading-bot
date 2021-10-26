# Install development tools

## Tools
```bash
curl -fsSL https://deb.nodesource.com/setup_14.x | sudo -E bash -
curl -sL https://dl.yarnpkg.com/debian/pubkey.gpg | gpg --dearmor | sudo tee /usr/share/keyrings/yarnkey.gpg >/dev/null
echo "deb [signed-by=/usr/share/keyrings/yarnkey.gpg] https://dl.yarnpkg.com/debian stable main" | sudo tee /etc/apt/sources.list.d/yarn.list
sudo apt update
sudo apt-get -y install bash curl file libglu1-mesa unzip xz-utils zip nodejs npm yarn
```

## [Vuepress](https://vuepress.vuejs.org/) (Documentation)
```bash
sudo npm install vuepress -g
```

## [Git](https://git-scm.com/)
```bash
sudo apt-get -y install git 
```

## [Java](https://openjdk.java.net/install/)
```bash
sudo apt-get -y install openjdk-11-jdk
```

## [Maven](https://maven.apache.org/)
```bash
sudo apt-get -y install maven
```

## [Intellij idea](https://www.jetbrains.com/idea/) (Optional)
```bash
sudo snap install intellij-idea-ultimate --stable
```
