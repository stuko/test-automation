import concurrent.futures
import math

PRIMES = [
    112272535095293,
    112582705942171,
    112272535095293,
    115280095190773,
    115797848077099,
    1099726899285419]

def is_prime(n):
    while True:
        if n % 2 == 0:
            sqrt_n = int(math.floor(math.sqrt(n)))
        for i in range(3, sqrt_n + 1, 2):
            if n % i == 0:
                print(n)

def call_sub(n1, n2, n3, n4):
    print(n1)
    print(n2)
    print(n3)
    print(n4)

def main():
    with concurrent.futures.ProcessPoolExecutor() as executor:
        executor.submit(call_sub,'hi','i am ','python ','beginer')

if __name__ == '__main__':
    main()