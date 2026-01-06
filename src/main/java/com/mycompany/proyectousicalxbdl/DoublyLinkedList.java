/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectousicalxbdl;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedList<E> implements Iterable<E> {

    private Node<E> first;
    private int current;

    class Node<E> {
        E data;
        Node<E> next;
        Node<E> previous;

        Node(E data) {
            this.data = data;
        }
    }

    public boolean isEmpty() {
        return current == 0;
    }

    public int size() {
        return current;
    }

    public boolean addLast(E e) {
        if (e == null) return false;
        Node<E> newNode = new Node<>(e);
        if (isEmpty()) {
            newNode.next = newNode.previous = newNode;
            first = newNode;
        } else {
            Node<E> last = first.previous;
            newNode.next = first;
            newNode.previous = last;
            last.next = newNode;
            first.previous = newNode;
        }
        current++;
        return true;
    }

    public boolean addFirst(E e) {
        if (addLast(e)) {
            first = first.previous;
            return true;
        }
        return false;
    }

    public E get(int index) {
        if (index < 0 || index >= current) throw new IndexOutOfBoundsException();
        Node<E> p = first;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }
        return p.data;
    }

    public E removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Lista vacía");
        E data = first.data;
        if (current == 1) {
            first = null;
        } else {
            Node<E> last = first.previous;
            first = first.next;
            first.previous = last;
            last.next = first;
        }
        current--;
        return data;
    }

    public E removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Lista vacía");
        Node<E> last = first.previous;
        E data = last.data;
        if (current == 1) {
            first = null;
        } else {
            Node<E> beforeLast = last.previous;
            beforeLast.next = first;
            first.previous = beforeLast;
        }
        current--;
        return data;
    }

    public E remove(int index) {
        if (index < 0 || index >= current) throw new IndexOutOfBoundsException();
        if (index == 0) return removeFirst();
        if (index == current - 1) return removeLast();

        Node<E> p = first;
        for (int i = 0; i < index; i++) p = p.next;

        p.previous.next = p.next;
        p.next.previous = p.previous;
        current--;
        return p.data;
    }

    public void clear() {
        first = null;
        current = 0;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        Node<E> p = first;
        for (int i = 0; i < current; i++) {
            sb.append(p.data);
            if (i < current - 1) sb.append(", ");
            p = p.next;
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Node<E> p = first;
            int count = 0;

            @Override
            public boolean hasNext() {
                return count < current;
            }

            @Override
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                E data = p.data;
                p = p.next;
                count++;
                return data;
            }
        };
    }
}
