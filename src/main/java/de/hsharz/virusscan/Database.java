package de.hsharz.virusscan;

import java.beans.PropertyChangeSupport;
import java.io.*;
import java.nio.file.Files;
import java.util.TreeSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

public class Database {

    public PropertyChangeSupport pCS = new PropertyChangeSupport(this);

    private TreeSet<Entity> entities;
    private boolean isLoaded = false;
    private final Object lock = new Object();

    public Database(File dirOrFile){
        entities = new TreeSet<>();
        new Thread(()-> {
            /*try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            if(dirOrFile.isDirectory()){
                // Lade CSV-Dateien aus dem Verzeichnis
                readDir(dirOrFile);
            } else if(dirOrFile.exists() && dirOrFile.isFile()){
                // Lade serialisierte Daten aus db.scan
                load();
            }
            synchronized (lock){
                isLoaded = true;
                lock.notifyAll();
            }

            pCS.firePropertyChange("loaded", false, true);
            System.out.println("LOADED!");
        }).start();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    private void readDir(File d){
        for(File file : d.listFiles()){
            readFile(file);
        }

    }

    private void readFile(File f){
        if(f == null){
            return;
        }
        try{
            Files.lines(f.toPath()).skip(1).map(l -> l.split(",")).map(Entity::fromStringArray).forEach(e -> entities.add(e));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean contains(Entity entity){
        synchronized(lock){
            while(!isLoaded){
                try{
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return entities.contains(entity);
    }

    public boolean contains(String hash){
        synchronized(lock){
            while(!isLoaded){
                try{
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return entities.stream().anyMatch(e -> e.hash().equals(hash));
    }

    public boolean add(Entity entity){
        synchronized(lock) {
            while (!isLoaded) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        if (entities.add(entity)) {
            System.out.println("Entity added: " + entity.hash());
            save();
            return true;
        }
        System.out.println("Entity already exists: " + entity.hash());
        return false;
    }

    public boolean add(String hash, String fileName){
        Entity entity = new Entity(hash, "", "", fileName, ThreatLevel.CRITICAL);
        return add(entity);
    }

    public boolean remove(Entity entity){
        synchronized(lock){
            while(!isLoaded){
                try{
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        if(entities.remove(entity)) {
            System.out.println("Entity removed: " + entity.hash());
            save();
            return true;
        }
        return false;
    }

    public boolean remove(String hash){
        Entity entity = entities.stream().filter(e -> e.hash().equals(hash)).findFirst().orElse(null);
        if(entity == null){
            return false;
        }
        return remove(entity);
    }

    private void save() {
        new Thread(() -> {
            synchronized (lock) {
                while (!isLoaded) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            //isLoaded = false;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("/home/linusr/Schreibtisch/IdeaProjects/Fortgeschrittene_Programmierung/VirusScan/dbFile/db.scan")))) {
                oos.writeObject(entities);
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            synchronized (lock){
                isLoaded = true;
                lock.notifyAll();
            }
            System.out.println("Database saved successfully");
        }).start();
    }

    private void load() {
        new Thread(() -> {
            synchronized (lock) {
                while (!isLoaded) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            isLoaded = false;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("/home/linusr/Schreibtisch/IdeaProjects/Fortgeschrittene_Programmierung/VirusScan/dbFile/db.scan")))) {
                entities = (TreeSet<Entity>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            synchronized (lock){
                isLoaded = true;
                lock.notifyAll();
            }
            System.out.println("Database loaded successfully");
        }).start();
    }

}
